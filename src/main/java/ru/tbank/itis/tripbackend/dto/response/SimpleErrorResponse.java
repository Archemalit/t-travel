package ru.tbank.itis.tripbackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Простой ответ на случай общей ошибки")
public record SimpleErrorResponse(
        @Schema(description = "Время возникновения ошибки", example = "2025-06-08T12:00:00Z")
        LocalDateTime timestamp,

        @Schema(description = "HTTP-статус ошибки", example = "404")
        int status,

        @Schema(description = "Название типа ошибки", example = "Поездка не найдена")
        String error,

        @Schema(description = "Подробное описание ошибки", example = "Поездка с ID 999 не найдена")
        String message
) {}