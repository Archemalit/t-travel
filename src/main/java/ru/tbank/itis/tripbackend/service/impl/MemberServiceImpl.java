package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dto.TripParticipantDto;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
import ru.tbank.itis.tripbackend.exception.*;
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

        if (tripInvitationRepository.existsByTripIdAndInvitedUserIdAndStatus(tripId, invitedUser.getId(), ForTripAndInvitationStatus.ACTIVE)) {
            throw new ValidationException("Активное приглашение для этого пользователя уже существует");
        }

        TripInvitation invitation = TripInvitation.builder()
                .trip(trip)
                .invitedUser(invitedUser)
                .inviter(creator)
                .status(ForTripAndInvitationStatus.ACTIVE)
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
                    invitation.setStatus(ForTripAndInvitationStatus.ARCHIVED);
                    tripInvitationRepository.save(invitation);
                });

        return SimpleResponse.builder()
                .success(true)
                .message("Участник успешно удален из поездки")
                .build();
    }

    @Override
    public List<TripParticipantDto> getActiveMembers(Long tripId, User currentUser) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        boolean isParticipant = tripParticipantRepository.existsByTripIdAndUserId(tripId, currentUser.getId());
        if (!isParticipant) {
            throw new ForbiddenAccessException("Только участники поездки могут просматривать список участников");
        }

        if (trip.getStatus() == ForTripAndInvitationStatus.ARCHIVED) {
            throw new ForbiddenAccessException("Поездка находится в архиве");
        }

        return tripParticipantRepository.findAllByTripIdAndStatus(tripId, TripParticipantStatus.ACCEPTED)
                .stream()
                .map(participant -> TripParticipantDto.builder()
                        .id(participant.getId())
                        .status(participant.getStatus().name())
                        .tripId(participant.getTrip().getId())
                        .userId(participant.getUser().getId())
                        .build())
                .collect(Collectors.toList());
    }
}