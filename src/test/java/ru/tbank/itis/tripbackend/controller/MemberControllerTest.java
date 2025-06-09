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
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
import ru.tbank.itis.tripbackend.exception.*;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.MemberService;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl userDetails;
    private InviteRequest inviteRequest;

    @BeforeEach
    void setUp() {
        User mockUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("79999999999")
                .password("password")
                .role(UserRole.USER)
                .build();

        userDetails = new UserDetailsImpl(mockUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        inviteRequest = InviteRequest.builder()
                .phone("79998887766")
                .build();
    }

    @Test
    @DisplayName("POST /trips/{tripId}/members — успешно приглашён новый участник — возвращает CREATED")
    void inviteMember_shouldReturnCreated() throws Exception {
        doNothing().when(memberService).inviteMember(eq(1L), any(User.class), eq(inviteRequest));

        mockMvc.perform(post("/api/v1/trips/1/members")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(inviteRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(memberService).inviteMember(eq(1L), any(User.class), eq(inviteRequest));
    }

    @Test
    @DisplayName("POST /trips/{tripId}/members — поездка не найдена — выбрасывает TripNotFoundException")
    void inviteMember_whenTripNotFound_shouldThrowTripNotFoundException() throws Exception {
        doThrow(new TripNotFoundException(1L)).when(memberService).inviteMember(eq(1L), any(User.class), eq(inviteRequest));

        mockMvc.perform(post("/api/v1/trips/1/members")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(inviteRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(memberService).inviteMember(eq(1L), any(User.class), eq(inviteRequest));
    }

    @Test
    @DisplayName("POST /trips/{tripId}/members — пользователь не создатель — выбрасывает ForbiddenAccessException")
    void inviteMember_whenUserNotCreator_shouldThrowForbiddenAccessException() throws Exception {
        doThrow(new ForbiddenAccessException("Только создатель поездки может приглашать участников"))
                .when(memberService).inviteMember(eq(1L), any(User.class), eq(inviteRequest));

        mockMvc.perform(post("/api/v1/trips/1/members")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(inviteRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(memberService).inviteMember(eq(1L), any(User.class), eq(inviteRequest));
    }

    @Test
    @DisplayName("POST /trips/{tripId}/members — пользователь уже участник — выбрасывает ValidationException")
    void inviteMember_whenUserAlreadyParticipant_shouldThrowValidationException() throws Exception {
        doThrow(new ValidationException("Пользователь уже является участником поездки или отклонил приглашение"))
                .when(memberService).inviteMember(eq(1L), any(User.class), eq(inviteRequest));

        mockMvc.perform(post("/api/v1/trips/1/members")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(inviteRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(memberService).inviteMember(eq(1L), any(User.class), eq(inviteRequest));
    }

    @Test
    @DisplayName("POST /trips/{tripId}/members — активное приглашение уже есть — выбрасывает ValidationException")
    void inviteMember_whenActiveInvitationExists_shouldThrowValidationException() throws Exception {
        doThrow(new ValidationException("Активное приглашение для этого пользователя уже существует"))
                .when(memberService).inviteMember(eq(1L), any(User.class), eq(inviteRequest));

        mockMvc.perform(post("/api/v1/trips/1/members")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(inviteRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(memberService).inviteMember(eq(1L), any(User.class), eq(inviteRequest));
    }

}