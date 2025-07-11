package ru.tbank.itis.tripbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о статусе участника поездки")
public class TripParticipantDto {

    @Schema(description = "Уникальный идентификатор участника", example = "1")
    private Long id;

    @Schema(description = "Статус участника", example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Статус участника обязателен")
    private String status;

    @Schema(description = "ID поездки", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID поездки обязательно")
    private Long tripId;

    @Schema(description = "ID пользователя", example = "200", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID пользователя обязательно")
    private Long userId;

}
