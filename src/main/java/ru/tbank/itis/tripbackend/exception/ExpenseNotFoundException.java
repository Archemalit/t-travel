package ru.tbank.itis.tripbackend.exception;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException(Long id) {
        super("Не найдено траты с id: " + id);
    }
}
