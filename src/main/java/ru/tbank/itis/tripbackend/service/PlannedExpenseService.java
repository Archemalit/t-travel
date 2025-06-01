package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.PlannedExpenseDto;
import ru.tbank.itis.tripbackend.model.PlannedExpense;

import java.util.List;

public interface PlannedExpenseService {
    PlannedExpenseDto getExpenseById(Long id);
    List<PlannedExpenseDto> getAllExpensesByTripId(Long tripId);
    PlannedExpenseDto createExpense(Long tripId, PlannedExpenseDto expenseDto);
    PlannedExpenseDto updateExpense(Long tripId, Long expenseId, PlannedExpenseDto expenseDto);
    void deleteExpense(Long tripId, Long expenseId);
}
