package ru.tbank.itis.tripbackend.exception.expense;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException(Long id) {super("Не найдено расхода с id: " + id);}
}
