package ru.tbank.itis.notificationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.itis.notificationservice.dictionary.NotificationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @NotNull(message = "Токен пользователя обязателен")
    private String deviceToken;

    @NotNull(message = "Тип уведомления обязателен")
    private NotificationType type;

    @NotBlank(message = "Сообщение обязательно")
    private String message;

    @NotNull(message = "Статус прочтения обязателен")
    private Boolean isRead;
}