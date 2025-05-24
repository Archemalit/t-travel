package ru.tbank.itis.tripbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.itis.tripbackend.dictonary.ExpenseCategory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualExpenseDto {

    private Long id;
    private Long tripId;

    @NotNull(message = "Сумма расхода обязательна")
    @Positive(message = "Сумма расхода должна быть положительной")
    private Double amount;

    @NotNull(message = "Категория расхода обязательна")
    private ExpenseCategory category;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    @NotBlank(message = "Скриншот чека обязателен")
    private String chequeImage;

    private Long paidByUserId;
    private Long[] membersIds;
}
