package ru.tbank.itis.tripbackend.dto;

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
public class DebtDto {

    private Long id;

    private Long tripId;

    private BigDecimal amount;

    private Long debtorId;

    private Long creditorId;

}
