package ru.tbank.itis.tripbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о долге между участниками поездки")
public class DebtDto {

//    @Schema(description = "Уникальный идентификатор долга", example = "1")
//    private Long id;

    @Schema(description = "ID поездки", example = "100")
    private Long tripId;

    @Schema(description = "Сумма долга", example = "500.0")
    private BigDecimal amount;

    @Schema(description = "ID должника", example = "2")
    private Long debtorId;

    @Schema(description = "ID кредитора", example = "3")
    private Long creditorId;
}