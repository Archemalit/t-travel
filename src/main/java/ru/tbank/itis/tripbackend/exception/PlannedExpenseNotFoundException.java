package ru.tbank.itis.tripbackend.exception;

public class PlannedExpenseNotFoundException extends RuntimeException {
    public PlannedExpenseNotFoundException(Long expenseId) { super("Не найден заплвнированный расход с id: " + expenseId); }
}
