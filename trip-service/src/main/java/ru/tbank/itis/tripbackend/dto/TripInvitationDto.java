package ru.tbank.itis.tripbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о приглашении в поездку")
public class TripInvitationDto {

    @Schema(description = "Уникальный идентификатор приглашения", example = "1")
    private Long id;

    @Schema(description = "ID поездки", example = "100")
    @NotNull(message = "ID поездки обязательно")
    private Long tripId;

    @Schema(description = "ID приглашённого пользователя", example = "200")
    @NotNull(message = "ID приглашенного пользователя обязательно")
    private Long invitedUserId;

    @Schema(description = "ID приглашающего пользователя", example = "101")
    @NotNull(message = "ID приглашающего пользователя обязательно")
    private Long inviterId;

    @Schema(description = "Комментарий к приглашению", example = "Может быть пустым")
    private String comment;

    @Schema(description = "Статус приглашения", example = "ACTIVE")
    @NotBlank(message = "Статус приглашения обязателен")
    private String status;
}