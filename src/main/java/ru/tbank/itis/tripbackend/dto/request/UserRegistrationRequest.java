package ru.tbank.itis.tripbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.tbank.itis.tripbackend.annotation.PasswordsMatch;

@PasswordsMatch
public record UserRegistrationRequest(
        @NotBlank(message = "Имя не должно быть пустым")
        @Size(max = 25, message = "Имя должно быть не длиннее 25 символов")
        String firstName,

        @NotBlank(message = "Фамилия не должна быть пустой")
        @Size(max = 25, message = "Фамилия должна быть не длиннее 25 символов")
        String lastName,

        @NotBlank(message = "Номер телефона обязателен")
        @Pattern(
                regexp = "^7\\d{10}$",
                message = "Номер телефона должен быть в формате 7XXXXXXXXXX"
        )
        String phoneNumber,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, max = 100, message = "Пароль должен быть от 8 до 100 символов")
        String password,

        @NotBlank(message = "Повтор пароля обязателен")
        String repeatPassword
) {}
