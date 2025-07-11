package ru.tbank.itis.tripbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dictionary.UserRole;
import ru.tbank.itis.tripbackend.dto.TripParticipantDto;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
import ru.tbank.itis.tripbackend.exception.ForbiddenAccessException;
import ru.tbank.itis.tripbackend.exception.TripNotFoundException;
import ru.tbank.itis.tripbackend.exception.ValidationException;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.TripInvitation;
import ru.tbank.itis.tripbackend.model.TripParticipant;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.TripInvitationRepository;
import ru.tbank.itis.tripbackend.repository.TripParticipantRepository;
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.service.impl.MemberServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TripInvitationRepository tripInvitationRepository;

    @Mock
    private TripParticipantRepository tripParticipantRepository;

    @Mock
    private NotificationService notificationService;

    private User creator;
    private User inviterUser;
    private User invitedUser;
    private User participantUser;
    private Trip trip;
    private InviteRequest inviteRequest;
    private TripParticipant activeParticipant;

    @BeforeEach
    void setUp() {
        creator = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("79999999999")
                .role(UserRole.USER)
                .build();

        invitedUser = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .phoneNumber("79888888888")
                .role(UserRole.USER)
                .build();

        participantUser = User.builder()
                .id(3L)
                .firstName("Mike")
                .lastName("Johnson")
                .phoneNumber("79777777777")
                .role(UserRole.USER)
                .build();

        trip = Trip.builder()
                .id(1L)
                .title("Test Trip")
                .creator(creator)
                .status(ForTripAndInvitationStatus.ACTIVE)
                .build();

        inviteRequest = InviteRequest.builder()
                .phone("79888888888")
                .build();

        activeParticipant = TripParticipant.builder()
                .id(1L)
                .trip(trip)
                .user(participantUser)
                .status(TripParticipantStatus.ACCEPTED)
                .build();
    }

    @Test
    @DisplayName("inviteMember — успешно приглашён новый пользователь — возвращает SimpleResponse")
    void inviteMember_successfullyInvited_returnsSimpleResponse() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findUserByPhoneNumber("79888888888")).thenReturn(Optional.of(invitedUser));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 2L, List.of(TripParticipantStatus.ACCEPTED))).thenReturn(false);
        when(tripInvitationRepository.existsByTripIdAndInvitedUserIdAndStatus(1L, 2L, ForTripAndInvitationStatus.ACTIVE))
                .thenReturn(false);

        memberService.inviteMember(1L, creator, inviteRequest);

        verify(tripInvitationRepository).save(any(TripInvitation.class));
        verify(tripParticipantRepository).save(any(TripParticipant.class));
    }

    @Test
    @DisplayName("inviteMember — поездка не найдена — выбрасывает TripNotFoundException")
    void inviteMember_tripNotFound_throwsTripNotFoundException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.inviteMember(1L, creator, inviteRequest))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    @DisplayName("inviteMember — пользователь не создатель — выбрасывает ForbiddenAccessException")
    void inviteMember_userNotCreator_throwsForbiddenAccessException() {
        User notCreator = User.builder().id(999L).build();
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> memberService.inviteMember(1L, notCreator, inviteRequest))
                .isInstanceOf(ForbiddenAccessException.class);
    }

    @Test
    @DisplayName("inviteMember — пользователь уже участник — выбрасывает ValidationException")
    void inviteMember_userAlreadyParticipant_throwsValidationException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findUserByPhoneNumber("79888888888")).thenReturn(Optional.of(invitedUser));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 2L, List.of(TripParticipantStatus.ACCEPTED))).thenReturn(true);

        assertThatThrownBy(() -> memberService.inviteMember(1L, creator, inviteRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("inviteMember — активное приглашение уже есть — выбрасывает ValidationException")
    void inviteMember_activeInvitationExists_throwsValidationException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findUserByPhoneNumber("79888888888")).thenReturn(Optional.of(invitedUser));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 2L, List.of(TripParticipantStatus.ACCEPTED))).thenReturn(false);
        when(tripInvitationRepository.existsByTripIdAndInvitedUserIdAndStatus(1L, 2L, ForTripAndInvitationStatus.ACTIVE))
                .thenReturn(true);

        assertThatThrownBy(() -> memberService.inviteMember(1L, creator, inviteRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("getActiveMembers — успешное получение списка участников — возвращает список участников")
    void getActiveMembers_success_returnsParticipantList() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 1L,
                List.of(TripParticipantStatus.ACCEPTED, TripParticipantStatus.PENDING))).thenReturn(true);
        when(tripParticipantRepository.findAllByTripIdAndStatus(1L, TripParticipantStatus.ACCEPTED))
                .thenReturn(List.of(activeParticipant));

        List<TripParticipantDto> result = memberService.getActiveMembers(1L, creator);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(3L);
        assertThat(result.get(0).getStatus()).isEqualTo("ACCEPTED");
    }

    @Test
    @DisplayName("getActiveMembers — поездка не найдена — выбрасывает TripNotFoundException")
    void getActiveMembers_tripNotFound_throwsTripNotFoundException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getActiveMembers(1L, creator))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    @DisplayName("getActiveMembers — пользователь не участник — выбрасывает ForbiddenAccessException")
    void getActiveMembers_userNotParticipant_throwsForbiddenAccessException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 1L,
                List.of(TripParticipantStatus.ACCEPTED, TripParticipantStatus.PENDING))).thenReturn(false);

        assertThatThrownBy(() -> memberService.getActiveMembers(1L, creator))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("Только участники поездки могут просматривать список участников");
    }

    @Test
    @DisplayName("getActiveMembers — поездка в архиве — выбрасывает ForbiddenAccessException")
    void getActiveMembers_tripArchived_throwsForbiddenAccessException() {
        trip.setStatus(ForTripAndInvitationStatus.ARCHIVED);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 1L,
                List.of(TripParticipantStatus.ACCEPTED, TripParticipantStatus.PENDING))).thenReturn(true);

        assertThatThrownBy(() -> memberService.getActiveMembers(1L, creator))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("Поездка находится в архиве");
    }

}