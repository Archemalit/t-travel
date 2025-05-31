package ru.tbank.itis.tripbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на аутентификацию пользователя")
public record UserLoginRequest(
        @NotBlank(message = "Номер телефона обязателен")
        @Schema(description = "Номер телефона пользователя", example = "79999999999")
        String phoneNumber,

        @NotBlank(message = "Пароль обязателен")
        @Schema(description = "Пароль пользователя", example = "password123")
        String password

) {}
