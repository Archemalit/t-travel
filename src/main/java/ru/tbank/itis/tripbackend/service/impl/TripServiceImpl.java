package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;
import ru.tbank.itis.tripbackend.dictionary.NotificationType;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dto.request.TripRequest;
import ru.tbank.itis.tripbackend.dto.response.TripResponse;
import ru.tbank.itis.tripbackend.exception.ForbiddenAccessException;
import ru.tbank.itis.tripbackend.exception.TripNotFoundException;
import ru.tbank.itis.tripbackend.exception.ValidationException;
import ru.tbank.itis.tripbackend.mapper.TripMapper;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.TripParticipant;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.TripParticipantRepository;
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.service.NotificationService;
import ru.tbank.itis.tripbackend.service.TripService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripMapper tripMapper;
    private final NotificationService notificationService;

    @Override
    public List<TripResponse> getAllTripsByUserId(Long userId, boolean onlyCreator, boolean onlyArchive) {
        log.debug("Получение поездок для пользователя с ID: {}", userId);

        List<TripResponse> trips;
        if (onlyCreator) {
            log.info("Получаем только поездки, где пользователь создатель");
            trips = tripRepository.findByCreatorId(userId).stream()
                    .filter(trip -> {
                        if (onlyArchive) {
                            return trip.getStatus() == ForTripAndInvitationStatus.ARCHIVED;
                        }
                        return trip.getStatus() == ForTripAndInvitationStatus.ACTIVE;
                    })
                    .map(tripMapper::toDto)
                    .collect(Collectors.toList());

        } else {
            log.info("Получаем все поездки, где пользователь участник");
            trips = tripRepository.findByParticipantsUserId(userId).stream()
                    .filter(trip -> {
                        if (onlyArchive) {
                            return trip.getStatus() == ForTripAndInvitationStatus.ARCHIVED;
                        }
                        return trip.getStatus() == ForTripAndInvitationStatus.ACTIVE;
                    })
                    .map(tripMapper::toDto)
                    .collect(Collectors.toList());
        }

        log.info("Найдено {} поездок для пользователя с ID: {}", trips.size(), userId);
        return trips;
    }

    @Override
    @Transactional
    public TripResponse getTripById(Long tripId, Long userId) {
        log.debug("Получение поездки с ID: {} для пользователя с ID: {}", tripId, userId);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> {
                    log.warn("Поездка с ID: {} не найдена", tripId);
                    return new TripNotFoundException(tripId);
                });

        boolean isParticipant = tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn
                (tripId, userId, List.of(TripParticipantStatus.ACCEPTED, TripParticipantStatus.PENDING));

        if (!isParticipant) {
            log.warn("Пользователь с ID: {} не является участником поездки с ID: {}", userId, tripId);
            throw new ForbiddenAccessException("Доступа нет!");
        }

//        if (trip.getStatus() == ForTripAndInvitationStatus.ARCHIVED) {
//            throw new ForbiddenAccessException("Поездка находится в архиве");
//        }

        log.info("Поездка с ID: {} успешно найдена для пользователя с ID: {}", tripId, userId);
        return tripMapper.toDto(trip);
    }

    @Override
    public TripResponse createTrip(TripRequest tripRequest, User user) {
        log.info("Создание новой поездки пользователем с ID: {}", user.getId());

        if (!tripRequest.getEndDate().isAfter(tripRequest.getStartDate())) {
            throw new ValidationException("Дата конца поездки должна быть позже начала!");
        }

        Trip trip = tripMapper.toEntity(tripRequest);
        trip.setCreator(user);
        trip.setStatus(ForTripAndInvitationStatus.ACTIVE);

        TripParticipant participant = TripParticipant.builder()
                .status(TripParticipantStatus.ACCEPTED)
                .trip(trip)
                .user(user)
                .build();

        trip.setParticipants(Set.of(participant));
        Trip savedTrip = tripRepository.save(trip);
        // TOOD: status проверить
        log.info("Поездка с ID: {} успешно создана", savedTrip.getId());
        return tripMapper.toDto(savedTrip);
    }

    @Override
    public TripResponse updateTrip(Long tripId, TripRequest tripRequest, Long userId) {
        log.info("Обновление поездки с ID: {}", tripId);

        Trip existingTrip = tripRepository.findById(tripId)
                .orElseThrow(() -> {
                    log.warn("Поездка с ID: {} не найдена", tripId);
                    return new TripNotFoundException(tripId);
                });

        if (!existingTrip.getCreator().getId().equals(userId)) {
            log.warn("Пользователь с ID: {} не является владельцем поездки с ID: {}", userId, tripId);
            throw new ForbiddenAccessException("Доступа нет!");
        }

        if (tripRequest.getTotalBudget() < 0) {
            log.warn("Попытка установить отрицательный бюджет для поездки с ID: {}", tripId);
            throw new ValidationException("Бюджет не может быть отрицательным");
        }

//        if (existingTrip.getStatus() == ForTripAndInvitationStatus.ARCHIVED) {
//            throw new ForbiddenAccessException("Нельзя изменить поездку в архиве");
//        }

        existingTrip.setTitle(tripRequest.getTitle());
        existingTrip.setStartDate(tripRequest.getStartDate());
        existingTrip.setEndDate(tripRequest.getEndDate());
        existingTrip.setTotalBudget(tripRequest.getTotalBudget());

        Trip updatedTrip = tripRepository.save(existingTrip);
        log.info("Поездка с ID: {} успешно обновлена", tripId);

        String message = String.format("Поездка '%s' была изменена", updatedTrip.getTitle());
        notifyTripParticipants(updatedTrip, userId, message);

        return tripMapper.toDto(updatedTrip);
    }

    private void notifyTripParticipants(Trip trip, Long excludedUserId, String message) {
        trip.getParticipants().stream()
                .filter(p -> !p.getUser().getId().equals(excludedUserId))
                .forEach(participant -> {
                    notificationService.createAndSendNotification(
                            participant.getUser().getId(),
                            trip.getId(),
                            NotificationType.TRIP_UPDATED,
                            message);
                });
    }

    @Override
    public void deleteTrip(Long tripId, Long userId) {
        log.info("Удаление поездки с ID: {}", tripId);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> {
                    log.warn("Поездка с ID: {} не найдена", tripId);
                    return new TripNotFoundException(tripId);
                });

        if (!trip.getCreator().getId().equals(userId)) {
            log.warn("Пользователь с ID: {} не является владельцем поездки с ID: {}", userId, tripId);
            throw new ForbiddenAccessException("Доступа нет!");
        }

//        if (trip.getStatus() == ForTripAndInvitationStatus.ARCHIVED) {
//            throw new ForbiddenAccessException("Нельзя удалить поездку в архиве");
//        }

        tripRepository.deleteById(tripId);
        log.info("Поездка с ID: {} успешно удалена", tripId);
    }

    @Override
    @Transactional
    public void archiveTrip(Long tripId, Long userId) {
        log.info("Архивирование поездки с ID: {}", tripId);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> {
                    log.warn("Поездка с ID: {} не найдена", tripId);
                    return new TripNotFoundException(tripId);
                });

        boolean isParticipant = trip.getParticipants().stream()
                .anyMatch(participant -> participant.getUser().getId().equals(userId));

        if (!isParticipant) {
            log.warn("Пользователь с ID: {} не является участником поездки с ID: {}", userId, tripId);
            throw new ForbiddenAccessException("Доступа нет!");
        }

        trip.setStatus(ForTripAndInvitationStatus.ARCHIVED);
        tripRepository.save(trip);
        log.info("Поездка с ID: {} переведена в архив", tripId);
    }
}
