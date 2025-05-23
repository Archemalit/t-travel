package ru.tbank.itis.tripbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.tbank.itis.tripbackend.annotation.PasswordsMatch;

@PasswordsMatch
@Schema(description = "Запрос на регистрацию нового пользователя")
public record UserRegistrationRequest(
        @NotBlank(message = "Имя не должно быть пустым")
        @Size(max = 25, message = "Имя должно быть не длиннее 25 символов")
        @Schema(description = "Имя пользователя", example = "Иван")
        String firstName,

        @NotBlank(message = "Фамилия не должна быть пустой")
        @Size(max = 25, message = "Фамилия должна быть не длиннее 25 символов")
        @Schema(description = "Фамилия пользователя", example = "Иванов")
        String lastName,

        @NotBlank(message = "Номер телефона обязателен")
        @Pattern(
                regexp = "^7\\d{10}$",
                message = "Номер телефона должен быть в формате 7XXXXXXXXXX"
        )
        @Schema(description = "Номер телефона в формате 7XXXXXXXXXX", example = "79999999999")
        String phoneNumber,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, max = 100, message = "Пароль должен быть от 8 до 100 символов")
        @Schema(description = "Пароль пользователя", example = "password123")
        String password,

        @NotBlank(message = "Повтор пароля обязателен")
        @Schema(description = "Повтор пароля", example = "password123")
        String repeatPassword
) {}
