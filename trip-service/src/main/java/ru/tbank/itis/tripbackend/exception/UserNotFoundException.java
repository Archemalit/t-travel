package ru.tbank.itis.tripbackend.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("Пользователь с ID " + userId + " не найден");
    }
    
    public UserNotFoundException(String message) {
        super(message);
    }
}