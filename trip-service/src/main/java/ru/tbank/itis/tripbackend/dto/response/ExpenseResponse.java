package ru.tbank.itis.tripbackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.itis.tripbackend.dictionary.ExpenseCategory;

import java.math.BigDecimal;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о расходе")
public class ExpenseResponse {

    @Schema(description = "Уникальный идентификатор расхода", example = "1")
    private Long id;

    @Schema(description = "Описание расхода", example = "Обед в Париже", maxLength = 500)
    @Size(max = 500, message = "Описание должно быть менее 500 символов")
    private String description;

    @Schema(description = "Общая уплаченная сумма", example = "50.00")
    @NotNull(message = "Сумма обязательна")
    @Positive(message = "Сумма должна быть положительной")
    private BigDecimal totalAmount;

    @Schema(description = "ID поездки, к которой относится расход", example = "101")
    @NotNull(message = "ID поездки обязательно")
    private Long tripId;

    @Schema(description = "Категория расхода", example = "FLIGHT")
    @NotNull(message = "Категория расхода обязательна")
    private ExpenseCategory category;

    @Schema(description = "ID пользователя, который оплатил расход", example = "201")
    @NotNull(message = "ID пользователя, оплатившего расход, обязательно")
    private Long paidByUserId;

    @Schema(description = "Список участников, за которых был сделан платёж")
    @NotNull(message = "Список участников обязателен")
    private Set<ExpenseParticipantResponse> participants;

}
