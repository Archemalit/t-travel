package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
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
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.service.NotificationService;
import ru.tbank.itis.tripbackend.service.TripService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final NotificationService notificationService;

    @Override
    public List<TripResponse> getAllTripsByUserId(Long id, boolean onlyCreator) {
        if (onlyCreator) {
            return tripRepository.findByCreatorId(id).stream()
                    .filter(trip -> trip.getStatus() == ForTripAndInvitationStatus.ACTIVE)
                    .map(tripMapper::toDto)
                    .collect(Collectors.toList());
        }
        return tripRepository.findByParticipantsUserId(id).stream()
                .filter(trip -> trip.getStatus() == ForTripAndInvitationStatus.ACTIVE)
                .map(tripMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TripResponse getTripById(Long id, Long userId) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        boolean isParticipant = trip.getParticipants().stream()
                .anyMatch(participant -> participant.getUser().getId().equals(userId));

        if (!isParticipant) {
            throw new ForbiddenAccessException("Доступа нет!");
        }

//        if (trip.getStatus() == ForTripAndInvitationStatus.ARCHIVED) {
//            throw new ForbiddenAccessException("Поездка находится в архиве");
//        }

        return tripMapper.toDto(trip);
    }

    @Override
    public TripResponse createTrip(TripRequest tripRequest, User user) {
        Trip trip = tripMapper.toEntity(tripRequest);
        trip.setCreator(user);
        trip.setStatus(ForTripAndInvitationStatus.ACTIVE);
        TripParticipant tripParticipant =
                TripParticipant.builder()
                        .status(TripParticipantStatus.ACCEPTED)
                        .trip(trip)
                        .user(user)
                        .build();
        trip.setParticipants(Set.of(tripParticipant));
        tripRepository.save(trip);

        TripResponse response = tripMapper.toDto(trip);
        response.setStatus(trip.getStatus());
        return response;
    }

    @Override
    public TripResponse updateTrip(Long id, TripRequest tripRequest, Long userId) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        if (!existingTrip.getCreator().getId().equals(userId)) {
            throw new ForbiddenAccessException("Доступа нет!");
        }

        if (tripRequest.getTotalBudget() < 0) {
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
    public void deleteTrip(Long id, Long userId) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        if (!trip.getCreator().getId().equals(userId)) {
            throw new ForbiddenAccessException("Доступа нет!");
        }

//        if (trip.getStatus() == ForTripAndInvitationStatus.ARCHIVED) {
//            throw new ForbiddenAccessException("Нельзя удалить поездку в архиве");
//        }

        tripRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void archiveTrip(Long tripId, Long userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        boolean isParticipant = trip.getParticipants().stream()
                .anyMatch(participant -> participant.getUser().getId().equals(userId));

        if (!isParticipant) {
            throw new ForbiddenAccessException("Доступа нет!");
        }

        trip.setStatus(ForTripAndInvitationStatus.ARCHIVED);
        tripRepository.save(trip);
    }
}
