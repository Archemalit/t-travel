package ru.tbank.itis.tripbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление профиля пользователя")
public class UserUpdateProfileRequest {

    @NotBlank(message = "Имя обязательно")
    @Size(max = 50, message = "Имя не должно быть пустым и более 50 символов")
    @Schema(description = "Новое имя пользователя", example = "Иван")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(max = 50, message = "Фамилия не должна быть пустой и более 50 символов")
    @Schema(description = "Новая фамилия пользователя", example = "Петров")
    private String lastName;

}
