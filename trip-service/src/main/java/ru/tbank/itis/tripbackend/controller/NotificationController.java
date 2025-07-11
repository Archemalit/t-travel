package ru.tbank.itis.tripbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.NotificationDto;
import ru.tbank.itis.tripbackend.dictionary.NotificationType;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "API для управления уведомлениями пользователя")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(
            summary = "Получение всех уведомлений пользователя",
            description = "Возвращает список всех уведомлений, если пользователь авторизован"
    )
    public List<NotificationDto> getUserNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.getUserNotifications(userDetails.getId());
    }

    @PostMapping("/{notificationId}/read")
    @Operation(
            summary = "Отметка уведомления как прочитанное",
            description = "Позволяет пользователю отметить конкретное уведомление как прочитанное"
    )
    public NotificationDto markAsRead(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long notificationId) {
        return notificationService.markAsRead(notificationId, userDetails.getId());
    }

    @GetMapping("/unread-count")
    @Operation(
            summary = "Получение количества непрочитанных уведомлений",
            description = "Возвращает количество непрочитанных уведомлений у пользователя"
    )
    public long getUnreadCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.getUnreadCount(userDetails.getId());
    }

    @PostMapping("/register-device")
    @Operation(
            summary = "Регистрация токена устройства для push-уведомлений",
            description = "Позволяет зарегистрировать токен устройства"
    )
    public void registerDevice(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String token) {
        notificationService.registerDeviceToken(userDetails.getId(), token);
    }

    @PostMapping("/send-test")
    @Operation(
            summary = "Отправка тестового уведомления",
            description = "Используется для тестирования push-уведомлений"
    )
    public NotificationDto sendTestNotification(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam NotificationType type,
            @RequestParam String message,
            @RequestParam Long tripId) {
        return notificationService.createAndSendNotification(
                userDetails.getId(), tripId, type, message);
    }

}