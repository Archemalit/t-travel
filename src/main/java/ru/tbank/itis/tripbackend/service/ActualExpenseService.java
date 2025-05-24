package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.ActualExpenseDto;

import java.util.List;

public interface ActualExpenseService {
    List<ActualExpenseDto> getAllExpensesByTrip(Long tripId);
    List<ActualExpenseDto> getAllExpensesByTripMember(Long tripId, Long memberId);
    ActualExpenseDto createExpense(ActualExpenseDto expenseDto);
    ActualExpenseDto updateExpense(Long userId, Long tripId, Long expenseId, ActualExpenseDto expenseDto);
    void deleteExpense(Long userId, Long tripId, Long expenseId);
}
