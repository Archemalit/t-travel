package ru.tbank.itis.tripbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные о поездке")
public class TripRequest {
    @NotBlank(message = "Название обязательно")
    @Size(max = 100, message = "Название должно быть менее 100 символов")
    @Schema(description = "Название поездки", example = "Поездка в Москву")
    private String title;

    @NotNull(message = "Дата начала обязательна")
    @FutureOrPresent(message = "Дата начала должна быть в настоящем или будущем")
    @Schema(description = "Дата начала поездки", example = "2025-07-06")
    private LocalDate startDate;

    @NotNull(message = "Дата конца обязательна")
    @Future(message = "Дата конца должна быть в будущем")
    @Schema(description = "Дата окончания поездки", example = "2025-07-13")
    private LocalDate endDate;

    @NotNull(message = "Общий бюджет обязателен")
    @PositiveOrZero(message = "Общий бюджет должен быть положительным или нулевым")
    @Schema(description = "Общий бюджет поездки", example = "500.0")
    private Double totalBudget;
}