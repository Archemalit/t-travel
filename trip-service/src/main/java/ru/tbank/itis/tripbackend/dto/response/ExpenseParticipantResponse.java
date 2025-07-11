package ru.tbank.itis.tripbackend.dto.response;

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
@Schema(description = "Информация о участнике расхода")
public class ExpenseParticipantResponse {

    @Schema(description = "Уникальный идентификатор участия в расходе", example = "1001")
    private Long id;

    @Schema(description = "ID расхода", example = "1")
    @NotNull(message = "ID расхода обязательно")
    private Long expenseId;

    @Schema(description = "ID пользователя, за которого был произведён платёж", example = "202")
    @NotNull(message = "ID участника обязательно")
    private Long participantId;

    @Schema(description = "ID пользователя, оплатившего расход", example = "201")
    @NotNull(message = "ID пользователя, оплатившего расход, обязательно")
    private Long paidByUserId;

    @Schema(description = "Сумма, уплаченная за данного участника", example = "50.00")
    @NotNull(message = "Сумма обязательна")
    @Positive(message = "Сумма должна быть положительной")
    private BigDecimal amount;

}
