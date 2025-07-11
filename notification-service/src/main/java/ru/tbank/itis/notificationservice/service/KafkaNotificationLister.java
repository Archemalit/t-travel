package ru.tbank.itis.notificationservice.service;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.tbank.itis.notificationservice.dto.request.NotificationRequest;

@Component
@AllArgsConstructor
public class KafkaNotificationLister {
    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.name}", containerFactory = "containerFactory")
    public void consume(@Payload NotificationRequest request, Acknowledgment acknowledgment) {
        notificationService.sendPushNotification(request);
        acknowledgment.acknowledge();
    }
}
