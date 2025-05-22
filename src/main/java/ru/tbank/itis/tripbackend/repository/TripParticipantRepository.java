package ru.tbank.itis.tripbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.TripParticipant;

import java.util.List;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {
    @Query("select tp.trip from TripParticipant tp where tp.user.id = :id")
    List<Trip> findTripsByUserId(Long id);
}
