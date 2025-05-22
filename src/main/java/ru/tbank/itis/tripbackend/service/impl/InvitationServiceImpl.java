package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dictionary.InvitationStatus;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.exception.ForbiddenAccessException;
import ru.tbank.itis.tripbackend.exception.InvitationNotFoundException;
import ru.tbank.itis.tripbackend.exception.ParticipantNotFoundException;
import ru.tbank.itis.tripbackend.exception.ValidationException;
import ru.tbank.itis.tripbackend.mapper.TripInvitationMapper;
import ru.tbank.itis.tripbackend.model.TripInvitation;
import ru.tbank.itis.tripbackend.model.TripParticipant;
import ru.tbank.itis.tripbackend.repository.TripInvitationRepository;
import ru.tbank.itis.tripbackend.repository.TripParticipantRepository;
import ru.tbank.itis.tripbackend.service.InvitationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {
    private final TripInvitationRepository tripInvitationRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripInvitationMapper tripInvitationMapper;
    @Override
    public List<TripInvitationDto> getUserInvitations(Long userId) {
        return tripInvitationRepository.findAllByInvitedUserIdAndStatus(userId, InvitationStatus.ACTIVE)
                .stream()
                .map(tripInvitationMapper::tripInvitationToTripInvitationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SimpleResponse acceptInvitation(Long invitationId, Long userId) {
        TripInvitation invitation = tripInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException(invitationId));

        if (!invitation.getInvitedUser().getId().equals(userId)) {
            throw new ForbiddenAccessException("Вы не можете принять это приглашение");
        }

        if (invitation.getStatus() != InvitationStatus.ACTIVE) {
            throw new ValidationException("Приглашение не активно");
        }

        TripParticipant participant = tripParticipantRepository.findByTripIdAndUserId(
                invitation.getTrip().getId(),
                userId
        ).orElseThrow(() -> new ParticipantNotFoundException(invitation.getTrip().getId(), userId));

        participant.setStatus(TripParticipantStatus.ACCEPTED);
        tripParticipantRepository.save(participant);

        invitation.setStatus(InvitationStatus.ARCHIVED);
        tripInvitationRepository.save(invitation);

        return SimpleResponse.builder()
                .success(true)
                .message("Вы приняли приглашение в поездку " + invitation.getTrip().getTitle())
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse rejectInvitation(Long invitationId, Long userId) {
        TripInvitation invitation = tripInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException(invitationId));

        if (!invitation.getInvitedUser().getId().equals(userId)) {
            throw new ForbiddenAccessException("Вы не можете отклонить это приглашение");
        }

        if (invitation.getStatus() != InvitationStatus.ACTIVE) {
            throw new ValidationException("Приглашение не активно");
        }

        TripParticipant participant = tripParticipantRepository.findByTripIdAndUserId(
                invitation.getTrip().getId(),
                userId
        ).orElseThrow(() -> new ParticipantNotFoundException(invitation.getTrip().getId(), userId));

        participant.setStatus(TripParticipantStatus.REJECTED);
        tripParticipantRepository.save(participant);

        invitation.setStatus(InvitationStatus.ARCHIVED);
        tripInvitationRepository.save(invitation);

        return SimpleResponse.builder()
                .success(true)
                .message("Вы отклонили приглашение в поездку " + invitation.getTrip().getTitle())
                .build();
    }
}
