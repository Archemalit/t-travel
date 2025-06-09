package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.PlannedExpenseDto;
import ru.tbank.itis.tripbackend.model.PlannedExpense;

import java.util.List;

public interface PlannedExpenseService {
    PlannedExpenseDto getExpenseById(Long tripId, Long userId, Long id);
    List<PlannedExpenseDto> getAllExpensesByTripId(Long tripId, Long userId);
    PlannedExpenseDto createExpense(Long tripId, Long userId, PlannedExpenseDto expenseDto);
    PlannedExpenseDto updateExpense(Long tripId, Long userId, Long expenseId, PlannedExpenseDto expenseDto);
    void deleteExpense(Long tripId, Long userId, Long expenseId);
}
