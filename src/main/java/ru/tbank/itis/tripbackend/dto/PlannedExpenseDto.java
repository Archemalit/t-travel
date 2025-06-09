package ru.tbank.itis.tripbackend.dto;

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
public class PlannedExpenseDto {
    private Long id;
    private Long tripId;

    @NotBlank(message = "Заголовок не должен быть пустым")
    private String header;

    @NotNull(message = "Сумма расхода обязательна")
    @Positive(message = "Сумма расхода должна быть положительной")
    private Double amount;

    @NotNull(message = "Категория расхода обязательна")
    private ExpenseCategory category;
}
