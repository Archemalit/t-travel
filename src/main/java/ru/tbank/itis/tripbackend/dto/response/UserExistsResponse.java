package ru.tbank.itis.tripbackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с флагом существования пользователя")
public class UserExistsResponse {
    @Schema(description = "Флаг: существует ли пользователь", example = "true")
    private boolean exists;
}