package ru.tbank.itis.tripbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.itis.tripbackend.model.TripInvitation;
import ru.tbank.itis.tripbackend.dictionary.InvitationStatus;

import java.util.List;

@Repository
public interface TripInvitationRepository extends JpaRepository<TripInvitation, Long> {
    List<TripInvitation> findAllByInvitedUserIdAndStatus(Long userId, InvitationStatus status);
    boolean existsByTripIdAndInvitedUserIdAndStatus(Long tripId, Long invitedUserId, InvitationStatus status);

    List<TripInvitation> findAllByTripIdAndInvitedUserId(Long tripId, Long invitedUserId);
}