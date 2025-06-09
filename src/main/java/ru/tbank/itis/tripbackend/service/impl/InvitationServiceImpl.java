package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;
import ru.tbank.itis.tripbackend.dictionary.NotificationType;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.exception.*;
import ru.tbank.itis.tripbackend.mapper.TripInvitationMapper;
import ru.tbank.itis.tripbackend.model.TripInvitation;
import ru.tbank.itis.tripbackend.model.TripParticipant;
import ru.tbank.itis.tripbackend.repository.TripInvitationRepository;
import ru.tbank.itis.tripbackend.repository.TripParticipantRepository;
import ru.tbank.itis.tripbackend.service.InvitationService;
import ru.tbank.itis.tripbackend.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {
    private final TripInvitationRepository tripInvitationRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripInvitationMapper tripInvitationMapper;
    private final NotificationService notificationService;
    @Override
    public List<TripInvitationDto> getUserInvitations(Long userId) {
        return tripInvitationRepository.findAllByInvitedUserIdAndStatus(userId, ForTripAndInvitationStatus.ACTIVE)
                .stream()
                .map(tripInvitationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptInvitation(Long invitationId, Long userId) {
        TripInvitation invitation = tripInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException(invitationId));

        if (!invitation.getInvitedUser().getId().equals(userId)) {
            throw new ForbiddenAccessException("Вы не можете принять это приглашение");
        }

        if (invitation.getStatus() != ForTripAndInvitationStatus.ACTIVE) {
            throw new ExpiredInvitationException("Статус приглашения: " + invitation.getStatus());
        }

        TripParticipant participant = tripParticipantRepository.findByTripIdAndUserIdAndStatus(
                invitation.getTrip().getId(),
                userId,
                TripParticipantStatus.PENDING
        ).orElseThrow(() -> new ParticipantNotFoundException(invitation.getTrip().getId(), userId));

        participant.setStatus(TripParticipantStatus.ACCEPTED);
        tripParticipantRepository.save(participant);

        invitation.setStatus(ForTripAndInvitationStatus.ARCHIVED);
        tripInvitationRepository.save(invitation);

        String message = String.format("%s принял ваше приглашение в поездку '%s'",
                invitation.getInvitedUser().getFirstName(),
                invitation.getTrip().getTitle());

        notificationService.createAndSendNotification(
                invitation.getInviter().getId(),
                invitation.getTrip().getId(),
                NotificationType.TRIP_INVITATION_RESPONSE,
                message);
    }

    @Override
    @Transactional
    public void rejectInvitation(Long invitationId, Long userId) {
        TripInvitation invitation = tripInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException(invitationId));

        if (!invitation.getInvitedUser().getId().equals(userId)) {
            throw new ForbiddenAccessException("Вы не можете отклонить это приглашение");
        }

        if (invitation.getStatus() != ForTripAndInvitationStatus.ACTIVE) {
            throw new ValidationException("Приглашение не активно");
        }

        TripParticipant participant = tripParticipantRepository.findByTripIdAndUserIdAndStatus(
                invitation.getTrip().getId(),
                userId,
                TripParticipantStatus.PENDING
        ).orElseThrow(() -> new ParticipantNotFoundException(invitation.getTrip().getId(), userId));

        participant.setStatus(TripParticipantStatus.REJECTED);
        tripParticipantRepository.save(participant);

        invitation.setStatus(ForTripAndInvitationStatus.ARCHIVED);
        tripInvitationRepository.save(invitation);

        String message = String.format("%s отклонил ваше приглашение в поездку '%s'",
                invitation.getInvitedUser().getFirstName(),
                invitation.getTrip().getTitle());

        notificationService.createAndSendNotification(
                invitation.getInviter().getId(),
                invitation.getTrip().getId(),
                NotificationType.TRIP_INVITATION_RESPONSE,
                message);
    }
}
