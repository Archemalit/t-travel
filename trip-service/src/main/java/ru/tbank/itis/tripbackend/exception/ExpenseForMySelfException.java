package ru.tbank.itis.tripbackend.exception;

public class ExpenseForMySelfException extends RuntimeException {
    public ExpenseForMySelfException() {
        super("Нельзя записать расход только на самого себя!");
    }
}
