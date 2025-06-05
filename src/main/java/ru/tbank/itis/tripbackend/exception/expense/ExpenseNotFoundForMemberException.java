package ru.tbank.itis.tripbackend.exception.expense;

public class ExpenseNotFoundForMemberException extends RuntimeException {
    public ExpenseNotFoundForMemberException(Long memberId) {
        super("Не найдено расходов для участника с id: " + memberId);
    }
}
