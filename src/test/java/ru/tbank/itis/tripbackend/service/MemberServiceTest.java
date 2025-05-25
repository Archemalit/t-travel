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
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
import ru.tbank.itis.tripbackend.exception.ForbiddenAccessException;
import ru.tbank.itis.tripbackend.exception.ParticipantNotFoundException;
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

    private User creator;
    private User invitedUser;
    private Trip trip;
    private InviteRequest inviteRequest;

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

        trip = Trip.builder()
                .id(1L)
                .title("Test Trip")
                .creator(creator)
                .build();

        inviteRequest = InviteRequest.builder()
                .phone("79888888888")
                .build();
    }

    @Test
    @DisplayName("inviteMember — успешно приглашён новый пользователь — возвращает SimpleResponse")
    void inviteMember_successfullyInvited_returnsSimpleResponse() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findUserByPhoneNumber("79888888888")).thenReturn(Optional.of(invitedUser));
        when(tripParticipantRepository.existsByTripIdAndUserId(1L, 2L)).thenReturn(false);
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
        when(tripParticipantRepository.existsByTripIdAndUserId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> memberService.inviteMember(1L, creator, inviteRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("inviteMember — активное приглашение уже есть — выбрасывает ValidationException")
    void inviteMember_activeInvitationExists_throwsValidationException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findUserByPhoneNumber("79888888888")).thenReturn(Optional.of(invitedUser));
        when(tripParticipantRepository.existsByTripIdAndUserId(1L, 2L)).thenReturn(false);
        when(tripInvitationRepository.existsByTripIdAndInvitedUserIdAndStatus(1L, 2L, ForTripAndInvitationStatus.ACTIVE))
                .thenReturn(true);

        assertThatThrownBy(() -> memberService.inviteMember(1L, creator, inviteRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("removeMember — успешно удален участник — возвращает SimpleResponse")
    void removeMember_successfullyRemoved_returnsSimpleResponse() {
        TripParticipant participant = TripParticipant.builder()
                .trip(trip)
                .user(invitedUser)
                .status(TripParticipantStatus.ACCEPTED)
                .build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripParticipantRepository.findByTripIdAndUserId(1L, 2L)).thenReturn(Optional.of(participant));
        when(tripInvitationRepository.findAllByTripIdAndInvitedUserId(1L, 2L)).thenReturn(List.of());

        memberService.removeMember(1L, 2L, creator);

        verify(tripParticipantRepository).delete(participant);
    }

    @Test
    @DisplayName("removeMember — поездка не найдена — выбрасывает TripNotFoundException")
    void removeMember_tripNotFound_throwsTripNotFoundException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.removeMember(1L, 2L, creator))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    @DisplayName("removeMember — пользователь не создатель — выбрасывает ForbiddenAccessException")
    void removeMember_userNotCreator_throwsForbiddenAccessException() {
        User notCreator = User.builder().id(999L).build();
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> memberService.removeMember(1L, 2L, notCreator))
                .isInstanceOf(ForbiddenAccessException.class);
    }

    @Test
    @DisplayName("removeMember — участник не найден — выбрасывает ParticipantNotFoundException")
    void removeMember_participantNotFound_throwsParticipantNotFoundException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripParticipantRepository.findByTripIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.removeMember(1L, 2L, creator))
                .isInstanceOf(ParticipantNotFoundException.class);
    }
}