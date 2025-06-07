package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;
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
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.service.TripService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    @Override
    public List<TripResponse> getAllTripsByUserId(Long id, boolean onlyCreator) {
        log.debug("Получение поездок для пользователя с ID: {}", id);

        List<TripResponse> trips;
        if (onlyCreator) {
            log.info("Получаем только поездки, где пользователь создатель");
            trips = tripRepository.findByCreatorId(id).stream()
                    .filter(trip -> trip.getStatus() == ForTripAndInvitationStatus.ACTIVE)
                    .map(tripMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            log.info("Получаем все поездки, где пользователь участник");
            trips = tripRepository.findByParticipantsUserId(id).stream()
                    .filter(trip -> trip.getStatus() == ForTripAndInvitationStatus.ACTIVE)
                    .map(tripMapper::toDto)
                    .collect(Collectors.toList());
        }

        log.info("Найдено {} поездок для пользователя с ID: {}", trips.size(), id);
        return trips;
    }

    @Override
    @Transactional
    public TripResponse getTripById(Long id, Long userId) {
        log.debug("Получение поездки с ID: {} для пользователя с ID: {}", id, userId);

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Поездка с ID: {} не найдена", id);
                    return new TripNotFoundException(id);
                });

        boolean isParticipant = trip.getParticipants().stream()
                .anyMatch(participant -> participant.getUser().getId().equals(userId));

        if (!isParticipant) {
            log.warn("Пользователь с ID: {} не является участником поездки с ID: {}", userId, id);
            throw new ForbiddenAccessException("Доступа нет!");
        }

        log.info("Поездка с ID: {} успешно найдена для пользователя с ID: {}", id, userId);
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

        log.info("Поездка с ID: {} успешно создана", savedTrip.getId());
        return tripMapper.toDto(savedTrip);
    }

    @Override
    public TripResponse updateTrip(Long id, TripRequest tripRequest, Long userId) {
        log.info("Обновление поездки с ID: {}", id);

        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Поездка с ID: {} не найдена", id);
                    return new TripNotFoundException(id);
                });

        if (!existingTrip.getCreator().getId().equals(userId)) {
            log.warn("Пользователь с ID: {} не является владельцем поездки с ID: {}", userId, id);
            throw new ForbiddenAccessException("Доступа нет!");
        }

        if (tripRequest.getTotalBudget() < 0) {
            log.warn("Попытка установить отрицательный бюджет для поездки с ID: {}", id);
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
        log.info("Поездка с ID: {} успешно обновлена", id);

        return tripMapper.toDto(updatedTrip);
    }

    @Override
    public void deleteTrip(Long id, Long userId) {
        log.info("Удаление поездки с ID: {}", id);

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Поездка с ID: {} не найдена", id);
                    return new TripNotFoundException(id);
                });

        if (!trip.getCreator().getId().equals(userId)) {
            log.warn("Пользователь с ID: {} не является владельцем поездки с ID: {}", userId, id);
            throw new ForbiddenAccessException("Доступа нет!");
        }

//        if (trip.getStatus() == ForTripAndInvitationStatus.ARCHIVED) {
//            throw new ForbiddenAccessException("Нельзя удалить поездку в архиве");
//        }

        tripRepository.deleteById(id);
        log.info("Поездка с ID: {} успешно удалена", id);
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
                .anyMatch(p -> p.getUser().getId().equals(userId));

        if (!isParticipant) {
            log.warn("Пользователь с ID: {} не является участником поездки с ID: {}", userId, tripId);
            throw new ForbiddenAccessException("Доступа нет!");
        }

        trip.setStatus(ForTripAndInvitationStatus.ARCHIVED);
        tripRepository.save(trip);
        log.info("Поездка с ID: {} переведена в архив", tripId);
    }
}
