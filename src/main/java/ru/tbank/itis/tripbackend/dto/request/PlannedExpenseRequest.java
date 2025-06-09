package ru.tbank.itis.tripbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.itis.tripbackend.dictionary.ExpenseCategory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Создание нового запланированного расхода")
public class PlannedExpenseRequest {

    @Schema(description = "Описание расхода", example = "Билеты на самолёт", maxLength = 500)
    @NotBlank(message = "Заголовок не должен быть пустым")
    private String header;

    @Schema(description = "Запланированный бюджет расхода", example = "5000.00")
    @NotNull(message = "Сумма расхода обязательна")
    @Positive(message = "Сумма расхода должна быть положительной")
    private Double amount;

    @Schema(description = "Категория расхода", example = "FLIGHT")
    @NotNull(message = "Категория расхода обязательна")
    private ExpenseCategory category;
}
