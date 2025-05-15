package ru.tbank.itis.tripbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.itis.tripbackend.model.TripParticipant;

import java.util.Optional;

@Repository
public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {
    Optional<TripParticipant> findByTripIdAndUserId(Long tripId, Long userId);
    boolean existsByTripIdAndUserId(Long tripId, Long userId);
}