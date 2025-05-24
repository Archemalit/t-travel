package ru.tbank.itis.tripbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {

    private Long id;

    @Size(max = 500, message = "Описание должно быть менее 500 символов")
    private String description;

    @NotNull(message = "ID поездки обязательно")
    private Long tripId;

    @NotNull(message = "ID пользователя, оплатившего расход, обязательно")
    private Long paidByUserId;

    @NotNull(message = "Список участников обязателен")
    private Set<ExpenseParticipantDto> participants;

}
