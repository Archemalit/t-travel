package ru.tbank.itis.tripbackend.dto.response;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {

    private Long id;

    @NotBlank(message = "Название обязательно")
    @Size(max = 100, message = "Название должно быть менее 100 символов")
    private String title;

    @Size(max = 500, message = "Описание должно быть менее 500 символов")
    private String description;

    @NotNull(message = "Дата начала обязательна")
    @FutureOrPresent(message = "Дата начала должна быть в настоящем или будущем")
    private LocalDate startDate;

    @NotNull(message = "Дата конца обязательна")
    @Future(message = "Дата конца должна быть в будущем")
    private LocalDate endDate;

    @NotNull(message = "Общий бюджет обязателен")
    @PositiveOrZero(message = "Общий бюджет должен быть положительным или нулевым")
    private Double totalBudget;
}