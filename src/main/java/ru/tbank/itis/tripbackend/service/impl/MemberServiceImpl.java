package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dictionary.InvitationStatus;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dto.*;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
import ru.tbank.itis.tripbackend.exception.*;
import ru.tbank.itis.tripbackend.mapper.TripInvitationMapper;
import ru.tbank.itis.tripbackend.mapper.TripParticipantMapper;
import ru.tbank.itis.tripbackend.model.*;
import ru.tbank.itis.tripbackend.repository.*;
import ru.tbank.itis.tripbackend.service.MemberService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripInvitationRepository tripInvitationRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripParticipantMapper tripParticipantMapper;

    @Override
    @Transactional
    public SimpleResponse inviteMember(Long tripId, User creator, InviteRequest inviteRequest) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        if (!trip.getCreator().getId().equals(creator.getId())) {
            throw new ForbiddenAccessException("Только создатель поездки может приглашать участников");
        }

        User invitedUser = userRepository.findUserByPhoneNumber(inviteRequest.getPhone())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с номером " + inviteRequest.getPhone() + " не найден"));

        if (tripParticipantRepository.existsByTripIdAndUserId(tripId, invitedUser.getId())) {
            throw new ValidationException("Пользователь уже является участником поездки или отклонил приглашение");
        }

        if (tripInvitationRepository.existsByTripIdAndInvitedUserIdAndStatus(tripId, invitedUser.getId(), InvitationStatus.ACTIVE)) {
            throw new ValidationException("Активное приглашение для этого пользователя уже существует");
        }

        TripInvitation invitation = TripInvitation.builder()
                .trip(trip)
                .invitedUser(invitedUser)
                .inviter(creator)
                .status(InvitationStatus.ACTIVE)
                .build();

        tripInvitationRepository.save(invitation);

        TripParticipant participant = TripParticipant.builder()
                .trip(trip)
                .user(invitedUser)
                .status(TripParticipantStatus.PENDING)
                .build();

        tripParticipantRepository.save(participant);

        return SimpleResponse.builder()
                .success(true)
                .message("Приглашение отправлено пользователю " + invitedUser.getFirstName() + " " + invitedUser.getLastName())
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse removeMember(Long tripId, Long userId, User creator) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        if (!trip.getCreator().getId().equals(creator.getId())) {
            throw new ForbiddenAccessException("Только создатель поездки может удалять участников");
        }

        TripParticipant participant = tripParticipantRepository.findByTripIdAndUserId(tripId, userId)
                .orElseThrow(() -> new ParticipantNotFoundException(tripId, userId));

        tripParticipantRepository.delete(participant);

        tripInvitationRepository.findAllByTripIdAndInvitedUserId(tripId, userId)
                .forEach(invitation -> {
                    invitation.setStatus(InvitationStatus.ARCHIVED);
                    tripInvitationRepository.save(invitation);
                });

        return SimpleResponse.builder()
                .success(true)
                .message("Участник успешно удален из поездки")
                .build();
    }
}