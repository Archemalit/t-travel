package ru.tbank.itis.tripbackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Ответ при ошибках валидации запроса")
public record ValidationErrorResponse(
        @Schema(description = "Время возникновения ошибки", example = "2025-06-08T12:00:00Z")
        LocalDateTime timestamp,

        @Schema(description = "HTTP-статус ошибки", example = "400")
        int status,

        @Schema(description = "Название типа ошибки", example = "Ошибка валидации данных")
        String error,

        @Schema(description = "Список ошибок по полям")
        List<ValidationError> errors
) {
    @Schema(description = "Детализация ошибки по конкретному полю")
    public record ValidationError(
            @Schema(description = "Имя невалидного поля", example = "phoneNumber")
            String field,

            @Schema(description = "Значение, которое вызвало ошибку", example = "19991234567")
            Object rejectedValue,

            @Schema(description = "Описание ошибки", example = "Номер телефона должен быть в формате 7XXXXXXXXXX")
            String message
    ) {}
}