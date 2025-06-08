package ru.tbank.itis.tripbackend.controller;

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
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> getUserNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.getUserNotifications(userDetails.getId());
    }

    @PostMapping("/{notificationId}/read")
    public NotificationDto markAsRead(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long notificationId) {
        return notificationService.markAsRead(notificationId, userDetails.getId());
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.getUnreadCount(userDetails.getId());
    }

    @PostMapping("/register-device")
    public void registerDevice(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String token) {
        notificationService.registerDeviceToken(userDetails.getId(), token);
    }

    @PostMapping("/send-test")
    public NotificationDto sendTestNotification(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam NotificationType type,
            @RequestParam String message,
            @RequestParam Long tripId) {
        return notificationService.createAndSendNotification(
                userDetails.getId(), tripId, type, message);
    }

}