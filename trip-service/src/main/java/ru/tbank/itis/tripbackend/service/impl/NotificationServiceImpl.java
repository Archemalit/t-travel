package ru.tbank.itis.tripbackend.service.impl;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dto.NotificationDto;
import ru.tbank.itis.tripbackend.dictionary.NotificationType;
import ru.tbank.itis.tripbackend.exception.TripNotFoundException;
import ru.tbank.itis.tripbackend.exception.UserNotFoundException;
import ru.tbank.itis.tripbackend.exception.NotificationNotFoundException;
import ru.tbank.itis.tripbackend.model.Notification;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.NotificationRepository;
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.mapper.NotificationMapper;
import ru.tbank.itis.tripbackend.service.NotificationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void saveDeviceToken(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setDeviceToken(token);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public NotificationDto createAndSendNotification(Long userId, Long tripId,
                                                     NotificationType type, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        Notification notification = Notification.builder()
                .message(message)
                .isRead(false)
                .type(type)
                .user(user)
                .trip(trip)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        if (user.getDeviceToken() != null) {
            sendPushNotification(user, type, message, tripId.toString());
        }

        return notificationMapper.toDto(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepository.findAllByUserIdOrderByIdDesc(userId).stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public NotificationDto markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toDto(updatedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    private void sendPushNotification(User user, NotificationType type,
                                      String message, String tripId) {
        try {
            String title = getNotificationTitle(type);

            com.google.firebase.messaging.Notification fcmNotification =
                    com.google.firebase.messaging.Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build();

            Message fcmMessage = Message.builder()
                    .setToken(user.getDeviceToken())
                    .setNotification(fcmNotification)
                    .putData("type", type.name())
                    .putData("tripId", tripId)
                    .build();

            firebaseMessaging.send(fcmMessage);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Не удалось отправить push-уведомление", e);
        }
    }

    private String getNotificationTitle(NotificationType type) {
        return switch (type) {
            case TRIP_INVITATION -> "Приглашение в поездку";
            case TRIP_INVITATION_RESPONSE -> "Ответ на приглашение в поездку";
            case NEW_EXPENSE -> "Новый расход";
            case DEBT_REMINDER -> "Напоминание о долге";
            case TRIP_UPDATED -> "Изменения в поездке";
            case PAYMENT_RECEIVED -> "Получен платеж";
        };
    }

    @Override
    @Transactional
    public void registerDeviceToken(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setDeviceToken(token);
        userRepository.save(user);
    }
}