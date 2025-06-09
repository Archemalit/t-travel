package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.request.ExpenseRequest;
import ru.tbank.itis.tripbackend.dto.response.ExpenseResponse;
import ru.tbank.itis.tripbackend.dto.response.ExpenseParticipantResponse;
import ru.tbank.itis.tripbackend.model.User;

import java.util.List;

public interface ActualExpenseService {
    ExpenseResponse getExpenseById(Long userId, Long expenseId);
    List<ExpenseResponse> getAllExpensesByTrip(Long userId, Long tripId);
//    List<ExpenseParticipantResponse> getAllExpensesByTripAndMember(Long tripId, Long memberId);
    ExpenseResponse createExpense(User paidBy, Long tripId, ExpenseRequest expenseDto);
    void deleteExpense(Long userId, Long expenseId);
}
