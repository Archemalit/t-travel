package ru.tbank.itis.tripbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.itis.tripbackend.dictionary.InvitationStatus;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dictionary.UserRole;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.exception.ForbiddenAccessException;
import ru.tbank.itis.tripbackend.exception.InvitationNotFoundException;
import ru.tbank.itis.tripbackend.exception.ParticipantNotFoundException;
import ru.tbank.itis.tripbackend.exception.ValidationException;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.TripInvitation;
import ru.tbank.itis.tripbackend.model.TripParticipant;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.TripInvitationRepository;
import ru.tbank.itis.tripbackend.repository.TripParticipantRepository;
import ru.tbank.itis.tripbackend.service.InvitationService;
import ru.tbank.itis.tripbackend.service.impl.InvitationServiceImpl;

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

    private User invitedUser;
    private Trip trip;
    private TripInvitation invitation;
    private TripParticipant participant;

    @BeforeEach
    void setUp() {
        invitedUser = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .phoneNumber("79999999999")
                .role(UserRole.USER)
                .build();

        trip = Trip.builder()
                .id(100L)
                .title("Test Trip")
                .build();

        invitation = TripInvitation.builder()
                .id(1L)
                .trip(trip)
                .invitedUser(invitedUser)
                .status(InvitationStatus.ACTIVE)
                .build();

        participant = TripParticipant.builder()
                .trip(trip)
                .user(invitedUser)
                .status(TripParticipantStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("acceptInvitation — успешно принято приглашение — обновляет статус участника и архивирует приглашение")
    void acceptInvitation_successfullyAccepted_returnsSimpleResponse() {
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(tripParticipantRepository.findByTripIdAndUserId(100L, 2L)).thenReturn(Optional.of(participant));

        SimpleResponse response = invitationService.acceptInvitation(1L, 2L);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Вы приняли приглашение в поездку Test Trip");
        assertThat(participant.getStatus()).isEqualTo(TripParticipantStatus.ACCEPTED);
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.ARCHIVED);

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
        invitation.setStatus(InvitationStatus.ARCHIVED);
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> invitationService.acceptInvitation(1L, 2L))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Приглашение не активно");

        verify(tripInvitationRepository, never()).save(any(TripInvitation.class));
        verify(tripParticipantRepository, never()).save(any(TripParticipant.class));
    }

    @Test
    @DisplayName("acceptInvitation — участник не найден — выбрасывает ParticipantNotFoundException")
    void acceptInvitation_participantNotFound_throwsParticipantNotFoundException() {
        when(tripInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(tripParticipantRepository.findByTripIdAndUserId(100L, 2L)).thenReturn(Optional.empty());

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
        when(tripParticipantRepository.findByTripIdAndUserId(100L, 2L)).thenReturn(Optional.of(participant));

        SimpleResponse response = invitationService.rejectInvitation(1L, 2L);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Вы отклонили приглашение в поездку Test Trip");
        assertThat(participant.getStatus()).isEqualTo(TripParticipantStatus.REJECTED);
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.ARCHIVED);

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
        invitation.setStatus(InvitationStatus.ARCHIVED);
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
        when(tripParticipantRepository.findByTripIdAndUserId(100L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invitationService.rejectInvitation(1L, 2L))
                .isInstanceOf(ParticipantNotFoundException.class)
                .hasMessage("Участник с ID 2 не найден в поездке с ID 100");

        verify(tripInvitationRepository, never()).save(any(TripInvitation.class));
        verify(tripParticipantRepository, never()).save(any(TripParticipant.class));
    }
}