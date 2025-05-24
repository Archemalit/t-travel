package ru.tbank.itis.tripbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripParticipantDto {

    private Long id;

    @NotNull(message = "Статус участника обязателен")
    private String status;

    @NotNull(message = "ID поездки обязательно")
    private Long tripId;

    @NotNull(message = "ID пользователя обязательно")
    private Long userId;

}
