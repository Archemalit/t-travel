package ru.tbank.itis.tripbackend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.itis.tripbackend.dto.NotificationDto;
import ru.tbank.itis.tripbackend.dictionary.NotificationType;
import ru.tbank.itis.tripbackend.exception.NotificationNotFoundException;
import ru.tbank.itis.tripbackend.exception.UserNotFoundException;
import ru.tbank.itis.tripbackend.mapper.NotificationMapper;
import ru.tbank.itis.tripbackend.model.Notification;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.NotificationRepository;
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.service.impl.NotificationServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TripRepository tripRepository;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Trip trip;
    private Notification notification;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setDeviceToken("device-token");

        trip = new Trip();
        trip.setId(1L);

        notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);
        notification.setTrip(trip);

        notificationDto = new NotificationDto();
        notificationDto.setId(1L);
    }

    @Test
    void saveDeviceToken_shouldSaveToken() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        notificationService.saveDeviceToken(1L, "new-token");

        assertEquals("new-token", user.getDeviceToken());
        verify(userRepository).save(user);
    }

    @Test
    void saveDeviceToken_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                notificationService.saveDeviceToken(1L, "token"));
    }

    @Test
    void createAndSendNotification_shouldCreateAndSend() throws FirebaseMessagingException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.toDto(notification)).thenReturn(notificationDto);

        NotificationDto result = notificationService.createAndSendNotification(
                1L, 1L, NotificationType.TRIP_INVITATION, "message");

        assertNotNull(result);
        verify(firebaseMessaging).send(any());
    }

    @Test
    void getUserNotifications_shouldReturnNotifications() {
        when(notificationRepository.findAllByUserIdOrderByIdDesc(1L)).thenReturn(List.of(notification));
        when(notificationMapper.toDto(notification)).thenReturn(notificationDto);

        List<NotificationDto> result = notificationService.getUserNotifications(1L);

        assertEquals(1, result.size());
        assertEquals(notificationDto, result.get(0));
    }

    @Test
    void markAsRead_shouldMarkNotification() {
        when(notificationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(notificationMapper.toDto(notification)).thenReturn(notificationDto);

        NotificationDto result = notificationService.markAsRead(1L, 1L);

        assertTrue(notification.isRead());
        assertEquals(notificationDto, result);
    }

    @Test
    void markAsRead_whenNotFound_shouldThrowException() {
        when(notificationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () ->
                notificationService.markAsRead(1L, 1L));
    }

    @Test
    void getUnreadCount_shouldReturnCount() {
        when(notificationRepository.countByUserIdAndIsReadFalse(1L)).thenReturn(5L);

        long result = notificationService.getUnreadCount(1L);

        assertEquals(5L, result);
    }

    @Test
    void registerDeviceToken_shouldRegisterToken() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        notificationService.registerDeviceToken(1L, "new-token");

        assertEquals("new-token", user.getDeviceToken());
        verify(userRepository).save(user);
    }
}