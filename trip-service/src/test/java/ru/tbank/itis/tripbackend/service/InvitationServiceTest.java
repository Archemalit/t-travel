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
import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.exception.*;
import ru.tbank.itis.tripbackend.mapper.TripInvitationMapper;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.TripInvitation;
import ru.tbank.itis.tripbackend.model.TripParticipant;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.TripInvitationRepository;
import ru.tbank.itis.tripbackend.repository.TripParticipantRepository;
import ru.tbank.itis.tripbackend.service.impl.InvitationServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

    @InjectMocks
    private InvitationServiceImpl invitationService;

    @Mock
    private TripInvitationRepository tripInvitationRepository;

    @Mock
    private TripParticipantRepository tripParticipantRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TripInvitationMapper tripInvitationMapper;

    private User creator;
    private User invitedUser;
    private Trip trip;
    private TripInvitation invitation;
    private TripInvitation activeInvitation;
    private TripInvitation archivedInvitation;
    private TripParticipant participant;
    private TripInvitationDto invitationDto;

    @BeforeEach
    void setUp() {
        creator = User.builder()
                .id(1L)
                .firstName("Bob")
                .lastName("Smith")
                .phoneNumber("79999999998")
                .role(UserRole.USER)
                .build();

        invitedUser = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .phoneNumber("79999999999")
                .role(UserRole.USER)
                .build();

        trip = Trip.builder()
                .id(100L)
                .creator(creator)
                .title("Test Trip")
                .build();

        invitation = TripInvitation.builder()
                .id(1L)
                .trip(trip)
                .inviter(creator)
                .invitedUser(invitedUser)
                .status(ForTripAndInvitationStatus.ACTIVE)
                .build();

        participant = TripParticipant.builder()
                .trip(trip)
                .user(invitedUser)
                .status(TripParticipantStatus.PENDING)
                .build();
        activeInvitation = TripInvitation.builder()
                .id(1L)
                .trip(trip)
                .inviter(creator)
                .invitedUser(invitedUser)
                .status(ForTripAndInvitationStatus.ACTIVE)
                .build();

        archivedInvitation = TripInvitation.builder()
                .id(2L)
                .trip(trip)
                .inviter(creator)
                .invitedUser(invitedUser)
                .status(ForTripAndInvitationStatus.ARCHIVED)
                .build();

        participant = TripParticipant.builder()
                .trip(trip)
                .user(invitedUser)
                .status(TripParticipantStatus.PENDING)
                .build();

        invitationDto = TripInvitationDto.builder()
                .id(1L)
                .tripId(100L)
                .inviterId(1L)
                .invitedUserId(2L)
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("getUserInvitations — есть активные приглашения — возвращает список активных приглашений")
    void getUserInvitations_withActiveInvitations_returnsActiveInvitations() {
        when(tripInvitationRepository.findAllByInvitedUserIdAndStatus(2L, ForTripAndInvitationStatus.ACTIVE))
                .thenReturn(List.of(activeInvitation));
        when(tripInvitationMapper.toDto(activeInvitation)).thenReturn(invitationDto);

        List<TripInvitationDto> result = invitationService.getUserInvitations(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");
        verify(tripInvitationRepository).findAllByInvitedUserIdAndStatus(2L, ForTripAndInvitationStatus.ACTIVE);
    }

    @Test
    @DisplayName("getUserInvitations — нет активных приглашений — возвращает пустой список")
    void getUserInvitations_noActiveInvitations_returnsEmptyList() {
        when(tripInvitationRepository.findAllByInvitedUserIdAndStatus(2L, ForTripAndInvitationStatus.ACTIVE))
                .thenReturn(List.of());

        List<TripInvitationDto> result = invitationService.getUserInvitations(2L);

        assertThat(result).isEmpty();
        verify(tripInvitationRepository).findAllByInvitedUserIdAndStatus(2L, ForTripAndInvitationStatus.ACTIVE);
    }

    @Test
    @DisplayName("getUserInvitations — проверка фильтрации только активных приглашений")
    void getUserInvitations_filtersOnlyActiveInvitations() {
        when(tripInvitationRepository.findAllByInvitedUserIdAndStatus(2L, ForTripAndInvitationStatus.ACTIVE))
                .thenReturn(List.of(activeInvitation));
        when(tripInvitationMapper.toDto(activeInvitation)).thenReturn(invitationDto);

        List<TripInvitationDto> result = invitationService.getUserInvitations(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");
        verify(tripInvitationRepository).findAllByInvitedUserIdAndStatus(2L, ForTripAndInvitationStatus.ACTIVE);
    }


    @Test
    @DisplayName("acceptInvitation — успешно принято приглашение — обновляет статус участника и архивирует приглашение")
    void acceptInvitation_successfullyAccepted_returnsSimpleResponse() {
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(tripParticipantRepository.findByTripIdAndUserIdAndStatus(100L, 2L, TripParticipantStatus.PENDING)).thenReturn(Optional.of(participant));

        invitationService.acceptInvitation(1L, 2L);

        assertThat(participant.getStatus()).isEqualTo(TripParticipantStatus.ACCEPTED);
        assertThat(invitation.getStatus()).isEqualTo(ForTripAndInvitationStatus.ARCHIVED);

        verify(tripParticipantRepository).save(participant);
        verify(tripInvitationRepository).save(invitation);
    }

    @Test
    @DisplayName("acceptInvitation — ID приглашения не существует — выбрасывает InvitationNotFoundException")
    void acceptInvitation_invitationNotFound_throwsInvitationNotFoundException() {
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invitationService.acceptInvitation(1L, 2L))
                .isInstanceOf(InvitationNotFoundException.class)
                .hasMessage("Приглашение с ID 1 не найдено");

        verify(tripInvitationRepository, never()).save(any(TripInvitation.class));
        verify(tripParticipantRepository, never()).save(any(TripParticipant.class));
    }

    @Test
    @DisplayName("acceptInvitation — пользователь не тот — выбрасывает ForbiddenAccessException")
    void acceptInvitation_userNotOwner_throwsForbiddenAccessException() {
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> invitationService.acceptInvitation(1L, 3L))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessage("Вы не можете принять это приглашение");

        verify(tripInvitationRepository, never()).save(any(TripInvitation.class));
        verify(tripParticipantRepository, never()).save(any(TripParticipant.class));
    }

    @Test
    @DisplayName("acceptInvitation — приглашение уже не активно — выбрасывает ValidationException")
    void acceptInvitation_invitationNotActive_throwsValidationException() {
        invitation.setStatus(ForTripAndInvitationStatus.ARCHIVED);
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> invitationService.acceptInvitation(1L, 2L))
                .isInstanceOf(ExpiredInvitationException.class)
                .hasMessage("Статус приглашения: ARCHIVED");

        verify(tripInvitationRepository, never()).save(any(TripInvitation.class));
        verify(tripParticipantRepository, never()).save(any(TripParticipant.class));
    }

    @Test
    @DisplayName("acceptInvitation — участник не найден — выбрасывает ParticipantNotFoundException")
    void acceptInvitation_participantNotFound_throwsParticipantNotFoundException() {
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(tripParticipantRepository.findByTripIdAndUserIdAndStatus(100L, 2L, TripParticipantStatus.PENDING))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> invitationService.acceptInvitation(1L, 2L))
                .isInstanceOf(ParticipantNotFoundException.class)
                .hasMessage("Участник с ID 2 не найден в поездке с ID 100");

        verify(tripInvitationRepository, never()).save(any(TripInvitation.class));
        verify(tripParticipantRepository, never()).save(any(TripParticipant.class));
    }

    @Test
    @DisplayName("rejectInvitation — успешно отклонено приглашение — обновляет статус участника и архивирует приглашение")
    void rejectInvitation_successfullyRejected_returnsSimpleResponse() {
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(tripParticipantRepository.findByTripIdAndUserIdAndStatus(100L, 2L, TripParticipantStatus.PENDING)).thenReturn(Optional.of(participant));

        invitationService.rejectInvitation(1L, 2L);

        assertThat(participant.getStatus()).isEqualTo(TripParticipantStatus.REJECTED);
        assertThat(invitation.getStatus()).isEqualTo(ForTripAndInvitationStatus.ARCHIVED);

        verify(tripParticipantRepository).save(participant);
        verify(tripInvitationRepository).save(invitation);
    }

    @Test
    @DisplayName("rejectInvitation — ID приглашения не существует — выбрасывает InvitationNotFoundException")
    void rejectInvitation_invitationNotFound_throwsInvitationNotFoundException() {
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invitationService.rejectInvitation(1L, 2L))
                .isInstanceOf(InvitationNotFoundException.class)
                .hasMessage("Приглашение с ID 1 не найдено");

        verify(tripInvitationRepository, never()).save(any(TripInvitation.class));
        verify(tripParticipantRepository, never()).save(any(TripParticipant.class));
    }

    @Test
    @DisplayName("rejectInvitation — пользователь не тот — выбрасывает ForbiddenAccessException")
    void rejectInvitation_userNotOwner_throwsForbiddenAccessException() {
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> invitationService.rejectInvitation(1L, 3L))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessage("Вы не можете отклонить это приглашение");

        verify(tripInvitationRepository, never()).save(any(TripInvitation.class));
        verify(tripParticipantRepository, never()).save(any(TripParticipant.class));
    }

    @Test
    @DisplayName("rejectInvitation — приглашение уже не активно — выбрасывает ValidationException")
    void rejectInvitation_invitationNotActive_throwsValidationException() {
        invitation.setStatus(ForTripAndInvitationStatus.ARCHIVED);
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> invitationService.rejectInvitation(1L, 2L))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Приглашение не активно");

        verify(tripInvitationRepository, never()).save(any(TripInvitation.class));
        verify(tripParticipantRepository, never()).save(any(TripParticipant.class));
    }

    @Test
    @DisplayName("rejectInvitation — участник не найден — выбрасывает ParticipantNotFoundException")
    void rejectInvitation_participantNotFound_throwsParticipantNotFoundException() {
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(tripParticipantRepository.findByTripIdAndUserIdAndStatus(100L, 2L, TripParticipantStatus.PENDING)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invitationService.rejectInvitation(1L, 2L))
                .isInstanceOf(ParticipantNotFoundException.class)
                .hasMessage("Участник с ID 2 не найден в поездке с ID 100");

        verify(tripInvitationRepository, never()).save(any(TripInvitation.class));
        verify(tripParticipantRepository, never()).save(any(TripParticipant.class));
    }
}