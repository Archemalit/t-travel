package ru.tbank.itis.tripbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "Запрос на приглашение пользователя в поездку")
public class InviteRequest {

    @Pattern(regexp = "^7\\d{10}$", message = "Номер телефона должен быть в международном формате")
    @Schema(
            description = "Номер телефона приглашаемого пользователя",
            example = "79999999998",
            implementation = String.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String phone;

}