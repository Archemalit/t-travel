package ru.tbank.itis.tripbackend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.itis.tripbackend.dictionary.NotificationType;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;

    @NotNull(message = "ID поездки обязательно")
    private Long tripId;

    @NotNull(message = "ID пользователя обязательно")
    private Long userId;

    @NotNull(message = "Тип уведомления обязателен")
    private NotificationType type;

    @NotBlank(message = "Сообщение обязательно")
    private String message;

    @NotNull(message = "Статус прочтения обязателен")
    private Boolean isRead;

}
