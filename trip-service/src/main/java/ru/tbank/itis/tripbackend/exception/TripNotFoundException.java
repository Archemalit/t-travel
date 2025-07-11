package ru.tbank.itis.tripbackend.exception;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(Long id) {
        super("Не найдено поездки с id: " + id);
    }
}
