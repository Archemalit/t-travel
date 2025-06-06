package ru.tbank.itis.tripbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Указание участника и суммы, которую он должен")
public class ExpenseParticipantRequest {

    @Schema(description = "ID пользователя, за которого оплачено", example = "202")
    @NotNull(message = "ID пользователя, за которого оплачено, обязателен")
    private Long participantId;

    @Schema(description = "Сумма, уплаченная за данного участника", example = "50.00")
    @NotNull(message = "Сумма обязательна")
    @Positive(message = "Сумма должна быть положительной")
    private BigDecimal amount;
}