package ru.tbank.itis.tripbackend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebtDto {

    private Long id;

    @NotNull(message = "Сумма долга обязательна")
    @Positive(message = "Сумма долга должна быть положительной")
    private BigDecimal amount;

    @NotNull(message = "ID поездки обязательно")
    private Long tripId;

    @NotNull(message = "ID должника обязательно")
    private Long debtorId;

    @NotNull(message = "ID кредитора обязательно")
    private Long creditorId;

}
