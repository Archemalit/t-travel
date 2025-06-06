package ru.tbank.itis.tripbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Создание нового расхода")
public class ExpenseRequest {

    @Schema(description = "Описание расхода", example = "Обед в Париже", maxLength = 500)
    @Size(max = 500, message = "Описание должно быть менее 500 символов")
    private String description;

    @Schema(description = "ID поездки, к которой относится расход", example = "101")
    @NotNull(message = "ID поездки обязательно")
    private Long tripId;

    @Schema(description = "ID пользователя, который оплатил расход", example = "201")
    @NotNull(message = "ID пользователя, оплатившего расход, обязательно")
    private Long paidByUserId;

    @Schema(description = "Список участников, за которых был сделан платёж")
    @NotNull(message = "Список участников обязательный")
    @NotEmpty(message = "Должен быть указан хотя бы один участник")
    private Set<ExpenseParticipantRequest> participants;
}
