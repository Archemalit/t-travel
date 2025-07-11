package ru.tbank.itis.tripbackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные профиля пользователя")
public class UserProfileResponse {
    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String lastName;

    @Schema(description = "Номер телефона пользователя", example = "79991234567")
    private String phoneNumber;

    @Schema(description = "Роль пользователя", example = "USER")
    private String role;
}
