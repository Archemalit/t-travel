package ru.tbank.itis.tripbackend.exception.expense;

public class ExpenseNotFoundForTripException extends RuntimeException {
    public ExpenseNotFoundForTripException(Long tripId) {
        super("Не найдено расходов по поездке с id: " + tripId);
    }
}
