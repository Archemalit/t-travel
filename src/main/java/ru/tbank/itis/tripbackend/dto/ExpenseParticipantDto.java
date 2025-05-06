package ru.tbank.itis.tripbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseParticipantDto {

    private Long id;

    @NotNull(message = "ID расхода обязательно")
    private Long expenseId;

    @NotNull(message = "ID участника обязательно")
    private Long participantId;

    @NotNull(message = "Сумма обязательна")
    @Positive(message = "Сумма должна быть положительной")
    private BigDecimal amount;

}
