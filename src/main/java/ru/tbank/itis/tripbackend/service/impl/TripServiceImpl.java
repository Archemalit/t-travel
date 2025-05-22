package ru.tbank.itis.tripbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    @Override
    public List<TripResponse> getAllTripsByUserId(Long id, boolean onlyCreator) {
        if (onlyCreator) {
            return tripRepository.findByCreatorId(id).stream()
                    .map(tripMapper::toDto)
                    .collect(Collectors.toList());
        }
        return tripRepository.findByParticipantsUserId(id).stream()
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
        return tripMapper.toDto(trip);
    }

    @Override
    public TripResponse createTrip(TripRequest tripRequest, User user) {
        Trip trip = tripMapper.toEntity(tripRequest);
        trip.setCreator(user);
        TripParticipant tripParticipant =
                TripParticipant.builder()
                        .status(TripParticipantStatus.ACCEPTED)
                        .trip(trip)
                        .user(user)
                        .build();
        trip.setParticipants(Set.of(tripParticipant));
        tripRepository.save(trip);
        return tripMapper.toDto(trip);
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

        existingTrip.setTitle(tripRequest.getTitle());
        existingTrip.setDescription(tripRequest.getDescription());
        existingTrip.setStartDate(tripRequest.getStartDate());
        existingTrip.setEndDate(tripRequest.getEndDate());
        existingTrip.setTotalBudget(tripRequest.getTotalBudget());

        Trip updatedTrip = tripRepository.save(existingTrip);
        return tripMapper.toDto(updatedTrip);
    }

    @Override
    public void deleteTrip(Long id, Long userId) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        if (!trip.getCreator().getId().equals(userId)) {
            throw new ForbiddenAccessException("Доступа нет!");
        }

        tripRepository.deleteById(id);
    }
}
