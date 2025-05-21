package ru.tbank.itis.tripbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.itis.tripbackend.model.Trip;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByCreatorId(Long creatorId);
    @Query("SELECT t FROM Trip t JOIN t.participants p WHERE p.user.id = :id")
    List<Trip> findByParticipantsUserId(@Param("id") Long id);
}
