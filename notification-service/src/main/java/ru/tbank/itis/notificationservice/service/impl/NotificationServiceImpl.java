package ru.tbank.itis.notificationservice.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.tbank.itis.notificationservice.dictionary.NotificationType;
import ru.tbank.itis.notificationservice.dto.request.NotificationRequest;
import ru.tbank.itis.notificationservice.service.NotificationService;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final FirebaseMessaging firebaseMessaging;
    public void sendPushNotification(NotificationRequest request) {
        try {
            NotificationType type = request.getType();
            String title = getNotificationTitle(type);

            Notification fcmNotification = Notification.builder()
                    .setTitle(title)
                    .setBody(request.getMessage())
                    .build();

            Message fcmMessage = Message.builder()
                    .setToken(request.getDeviceToken())
                    .setNotification(fcmNotification)
                    .putData("type", type.name())
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
}
