package ru.tbank.itis.tripbackend.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class Transaction {
    private Long debtorId;
    private Long creditorId;
    private BigDecimal amount;
}
