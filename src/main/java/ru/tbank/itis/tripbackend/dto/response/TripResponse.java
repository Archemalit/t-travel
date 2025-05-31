package ru.tbank.itis.tripbackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные о поездке")
public class TripResponse {

    @Schema(description = "Уникальный идентификатор поездки", example = "1")
    private Long id;

    @Schema(description = "Статус поездки (ACTIVE, ARCHIVED)", example = "ACTIVE")
    private ForTripAndInvitationStatus status;

    @NotBlank(message = "Название обязательно")
    @Size(max = 100, message = "Название должно быть менее 100 символов")
    @Schema(description = "Название поездки", example = "Поездка в Москву")
    private String title;

    @NotNull(message = "Дата начала обязательна")
    @Schema(description = "Дата начала поездки", example = "2025-07-06")
    private LocalDate startDate;

    @NotNull(message = "Дата конца обязательна")
    @Schema(description = "Дата окончания поездки", example = "2025-07-13")
    private LocalDate endDate;

    @NotNull(message = "Общий бюджет обязателен")
    @PositiveOrZero(message = "Общий бюджет должен быть положительным или нулевым")
    @Schema(description = "Общий бюджет поездки", example = "500.0")
    private Double totalBudget;
}