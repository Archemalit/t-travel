package ru.tbank.itis.tripbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.itis.tripbackend.dictionary.UserRole;
import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.exception.ForbiddenAccessException;
import ru.tbank.itis.tripbackend.exception.InvitationNotFoundException;
import ru.tbank.itis.tripbackend.exception.ParticipantNotFoundException;
import ru.tbank.itis.tripbackend.exception.ValidationException;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.InvitationService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvitationController.class)
@ExtendWith(MockitoExtension.class)
class InvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvitationService invitationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl userDetails;
    private TripInvitationDto invitationDto;

    @BeforeEach
    void setUp() {
        userDetails = new UserDetailsImpl(User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("79999999999")
                .role(UserRole.USER)
                .build());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        invitationDto = TripInvitationDto.builder()
                .id(1L)
                .tripId(100L)
                .invitedUserId(1L)
                .inviterId(2L)
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/invitations — успешно получены активные приглашения — возвращает список")
    void getUserInvitations_shouldReturnListOfInvitations() throws Exception {
        when(invitationService.getUserInvitations(1L)).thenReturn(List.of(invitationDto));

        mockMvc.perform(get("/api/v1/invitations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].tripId").value(100L));

        verify(invitationService).getUserInvitations(1L);
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{invitationId}/accept — успешно принято приглашение — возвращает OK")
    void acceptInvitation_successfullyAccepted_returnsOk() throws Exception {
        doNothing().when(invitationService).acceptInvitation(1L, 1L);

        mockMvc.perform(post("/api/v1/invitations/1/accept")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(invitationService).acceptInvitation(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{invitationId}/accept — приглашение не найдено — выбрасывает InvitationNotFoundException")
    void acceptInvitation_invitationNotFound_throwsInvitationNotFoundException() throws Exception {
        doThrow(new InvitationNotFoundException(1L)).when(invitationService).acceptInvitation(1L, 1L);

        mockMvc.perform(post("/api/v1/invitations/1/accept")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(invitationService).acceptInvitation(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{invitationId}/accept — пользователь не тот — выбрасывает ForbiddenAccessException")
    void acceptInvitation_userNotOwner_throwsForbiddenAccessException() throws Exception {
        doThrow(new ForbiddenAccessException("Вы не можете принять это приглашение"))
                .when(invitationService).acceptInvitation(1L, 1L);

        mockMvc.perform(post("/api/v1/invitations/1/accept")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(invitationService).acceptInvitation(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{invitationId}/accept — приглашение не активно — выбрасывает ValidationException")
    void acceptInvitation_invitationNotActive_throwsValidationException() throws Exception {
        doThrow(new ValidationException("Приглашение не активно"))
                .when(invitationService).acceptInvitation(1L, 1L);

        mockMvc.perform(post("/api/v1/invitations/1/accept")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(invitationService).acceptInvitation(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{invitationId}/accept — участник не найден — выбрасывает ParticipantNotFoundException")
    void acceptInvitation_participantNotFound_throwsParticipantNotFoundException() throws Exception {
        doThrow(new ParticipantNotFoundException(100L, 1L))
                .when(invitationService).acceptInvitation(1L, 1L);

        mockMvc.perform(post("/api/v1/invitations/1/accept")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(invitationService).acceptInvitation(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{invitationId}/reject — успешно отклонено приглашение — возвращает OK")
    void rejectInvitation_successfullyRejected_returnsOk() throws Exception {
        doNothing().when(invitationService).rejectInvitation(1L, 1L);

        mockMvc.perform(post("/api/v1/invitations/1/reject")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(invitationService).rejectInvitation(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{invitationId}/reject — приглашение не найдено — выбрасывает InvitationNotFoundException")
    void rejectInvitation_invitationNotFound_throwsInvitationNotFoundException() throws Exception {
        doThrow(new InvitationNotFoundException(1L)).when(invitationService).rejectInvitation(1L, 1L);

        mockMvc.perform(post("/api/v1/invitations/1/reject")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(invitationService).rejectInvitation(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{invitationId}/reject — пользователь не тот — выбрасывает ForbiddenAccessException")
    void rejectInvitation_userNotOwner_throwsForbiddenAccessException() throws Exception {
        doThrow(new ForbiddenAccessException("Вы не можете отклонить это приглашение"))
                .when(invitationService).rejectInvitation(1L, 1L);

        mockMvc.perform(post("/api/v1/invitations/1/reject")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(invitationService).rejectInvitation(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{invitationId}/reject — приглашение не активно — выбрасывает ValidationException")
    void rejectInvitation_invitationNotActive_throwsValidationException() throws Exception {
        doThrow(new ValidationException("Приглашение не активно"))
                .when(invitationService).rejectInvitation(1L, 1L);

        mockMvc.perform(post("/api/v1/invitations/1/reject")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(invitationService).rejectInvitation(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{invitationId}/reject — участник не найден — выбрасывает ParticipantNotFoundException")
    void rejectInvitation_participantNotFound_throwsParticipantNotFoundException() throws Exception {
        doThrow(new ParticipantNotFoundException(100L, 1L))
                .when(invitationService).rejectInvitation(1L, 1L);

        mockMvc.perform(post("/api/v1/invitations/1/reject")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(invitationService).rejectInvitation(1L, 1L);
    }
}