package ru.tbank.itis.tripbackend.exception;

// TODO: добавить в exception handler
public class PhoneNumberAlreadyTakenException extends RuntimeException {
    public PhoneNumberAlreadyTakenException(String message) {
        super(message);
    }
}