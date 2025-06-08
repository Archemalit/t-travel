package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.NotificationDto;
import ru.tbank.itis.tripbackend.dictionary.NotificationType;

import java.util.List;

public interface NotificationService {
    void saveDeviceToken(Long userId, String token);
    NotificationDto createAndSendNotification(Long userId, Long tripId, NotificationType type, String message);
    List<NotificationDto> getUserNotifications(Long userId);
    NotificationDto markAsRead(Long notificationId, Long userId);
    long getUnreadCount(Long userId);
    void registerDeviceToken(Long userId, String token);
}