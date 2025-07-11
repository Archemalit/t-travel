package ru.tbank.itis.tripbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.model.TripParticipant;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {
    Optional<TripParticipant> findByTripIdAndUserIdAndStatus(Long tripId, Long userId, TripParticipantStatus status);
    boolean existsByTripIdAndUserIdAndStatusIn(Long tripId, Long userId, List<TripParticipantStatus> status);
    List<TripParticipant> findAllByTripIdAndStatus(Long tripId, TripParticipantStatus status);
}