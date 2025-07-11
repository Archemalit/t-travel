package ru.tbank.itis.notificationservice.service;

import ru.tbank.itis.notificationservice.dictionary.NotificationType;
import ru.tbank.itis.notificationservice.dto.request.NotificationRequest;

public interface NotificationService {
    void sendPushNotification(NotificationRequest request);
}
