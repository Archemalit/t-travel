package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;
import ru.tbank.itis.tripbackend.dictionary.NotificationType;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dto.TripParticipantDto;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
import ru.tbank.itis.tripbackend.exception.*;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.TripInvitation;
import ru.tbank.itis.tripbackend.model.TripParticipant;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.TripInvitationRepository;
import ru.tbank.itis.tripbackend.repository.TripParticipantRepository;
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.service.MemberService;
import ru.tbank.itis.tripbackend.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripInvitationRepository tripInvitationRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void inviteMember(Long tripId, User creator, InviteRequest inviteRequest) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        if (!trip.getCreator().getId().equals(creator.getId())) {
            throw new ForbiddenAccessException("Только создатель поездки может приглашать участников");
        }

        User invitedUser = userRepository.findUserByPhoneNumber(inviteRequest.getPhone())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с номером " + inviteRequest.getPhone() + " не найден"));

        if (tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(tripId, invitedUser.getId(), List.of(TripParticipantStatus.ACCEPTED))) {
            throw new ValidationException("Пользователь уже является участником поездки или его уже пригласили");
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

        String message = String.format("%s %s пригласил вас в поездку '%s'",
                creator.getFirstName(),
                creator.getLastName(),
                trip.getTitle());

        notificationService.createAndSendNotification(
                invitedUser.getId(),
                tripId,
                NotificationType.TRIP_INVITATION,
                message);

        TripParticipant participant = TripParticipant.builder()
                .trip(trip)
                .user(invitedUser)
                .status(TripParticipantStatus.PENDING)
                .build();

        tripParticipantRepository.save(participant);
    }

//    @Override
//    @Transactional
//    public void removeMember(Long tripId, Long userId, User creator) {
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new TripNotFoundException(tripId));
//
//        if (!trip.getCreator().getId().equals(creator.getId()) && !creator.getId().equals(userId)) {
//            throw new ForbiddenAccessException("Только создатель поездки может удалять участников");
//        }
//
//        if (trip.getCreator().getId().equals(userId)) {
//            throw new ForbiddenAccessException("Создатель поездки не может удалить себя из поездки," +
//                    " но может удалить всю группу со всеми участниками");
//        }
//
//        TripParticipant participant = tripParticipantRepository.findByTripIdAndUserIdAndStatus(tripId, userId, TripParticipantStatus.ACCEPTED)
//                .orElseThrow(() -> new ParticipantNotFoundException(tripId, userId));
//
//        tripParticipantRepository.delete(participant);
//
//        tripInvitationRepository.findAllByTripIdAndInvitedUserId(tripId, userId)
//                .forEach(invitation -> {
//                    invitation.setStatus(ForTripAndInvitationStatus.ARCHIVED);
//                    tripInvitationRepository.save(invitation);
//                });
//    }

    @Override
    public List<TripParticipantDto> getActiveMembers(Long tripId, User currentUser) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        boolean isParticipant = tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(tripId, currentUser.getId(), List.of(TripParticipantStatus.ACCEPTED, TripParticipantStatus.PENDING));
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