package ru.tbank.itis.tripbackend.exception;

public class SeveralExpensesForUser extends RuntimeException {
    public SeveralExpensesForUser(Long userId) {
        super("На пользователя с ID " + userId + "нельзя сразу записать несколько расходов!");
    }
}
