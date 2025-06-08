package ru.tbank.itis.tripbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.itis.tripbackend.dto.NotificationDto;
import ru.tbank.itis.tripbackend.dictionary.NotificationType;
import ru.tbank.itis.tripbackend.dictionary.UserRole;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.NotificationService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("79999999999")
                .password("password")
                .role(UserRole.USER)
                .build();

        userDetails = new UserDetailsImpl(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getUserNotifications_shouldReturnNotifications() throws Exception {
        NotificationDto dto = NotificationDto.builder()
                .id(1L)
                .tripId(1L)
                .userId(1L)
                .type(NotificationType.TRIP_INVITATION)
                .message("Test message")
                .isRead(false)
                .build();

        when(notificationService.getUserNotifications(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tripId").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].type").value("TRIP_INVITATION"))
                .andExpect(jsonPath("$[0].message").value("Test message"))
                .andExpect(jsonPath("$[0].isRead").value(false));

        verify(notificationService).getUserNotifications(1L);
    }

    @Test
    void markAsRead_shouldMarkNotification() throws Exception {
        NotificationDto dto = NotificationDto.builder()
                .id(1L)
                .tripId(1L)
                .userId(1L)
                .type(NotificationType.TRIP_INVITATION)
                .message("Test message")
                .isRead(true)
                .build();

        when(notificationService.markAsRead(1L, 1L)).thenReturn(dto);

        mockMvc.perform(post("/api/v1/notifications/1/read")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tripId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.type").value("TRIP_INVITATION"))
                .andExpect(jsonPath("$.message").value("Test message"))
                .andExpect(jsonPath("$.isRead").value(true));

        verify(notificationService).markAsRead(1L, 1L);
    }

    @Test
    void getUnreadCount_shouldReturnCount() throws Exception {
        when(notificationService.getUnreadCount(1L)).thenReturn(3L);

        mockMvc.perform(get("/api/v1/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        verify(notificationService).getUnreadCount(1L);
    }

    @Test
    void registerDevice_shouldRegisterToken() throws Exception {
        doNothing().when(notificationService).registerDeviceToken(1L, "test-token");

        mockMvc.perform(post("/api/v1/notifications/register-device")
                        .with(csrf())
                        .param("token", "test-token"))
                .andExpect(status().isOk());

        verify(notificationService).registerDeviceToken(1L, "test-token");
    }

    @Test
    void sendTestNotification_shouldSendNotification() throws Exception {
        NotificationDto dto = NotificationDto.builder()
                .id(1L)
                .tripId(1L)
                .userId(1L)
                .type(NotificationType.TRIP_INVITATION)
                .message("test")
                .isRead(false)
                .build();

        when(notificationService.createAndSendNotification(1L, 1L,
                NotificationType.TRIP_INVITATION, "test")).thenReturn(dto);

        mockMvc.perform(post("/api/v1/notifications/send-test")
                        .with(csrf())
                        .param("type", "TRIP_INVITATION")
                        .param("message", "test")
                        .param("tripId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tripId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.type").value("TRIP_INVITATION"))
                .andExpect(jsonPath("$.message").value("test"))
                .andExpect(jsonPath("$.isRead").value(false));

        verify(notificationService).createAndSendNotification(1L, 1L,
                NotificationType.TRIP_INVITATION, "test");
    }
}