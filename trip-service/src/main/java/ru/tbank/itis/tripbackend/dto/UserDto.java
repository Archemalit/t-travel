package ru.tbank.itis.tripbackend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Size(max = 50, message = "Имя должно быть менее 50 символов")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(max = 50, message = "Фамилия должна быть менее 50 символов")
    private String lastName;

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Номер телефона должен быть в международном формате")
    private String phoneNumber;

    @NotNull(message = "Роль обязательна")
    private String role;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    private String password;

}
