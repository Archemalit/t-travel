package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.PlannedExpenseDto;
import ru.tbank.itis.tripbackend.dto.request.PlannedExpenseRequest;
import ru.tbank.itis.tripbackend.dto.response.PlannedExpenseResponse;
import ru.tbank.itis.tripbackend.model.PlannedExpense;

import java.util.List;

public interface PlannedExpenseService {
//    PlannedExpenseResponse getExpenseById(Long tripId, Long userId, Long id);
    List<PlannedExpenseResponse> getAllExpensesByTripId(Long tripId, Long userId);
    PlannedExpenseResponse createExpense(Long tripId, Long userId, PlannedExpenseRequest expenseDto);
//    PlannedExpenseDto updateExpense(Long tripId, Long userId, Long expenseId, PlannedExpenseRequest expenseDto);
    void deleteExpense(Long userId, Long expenseId);
}
