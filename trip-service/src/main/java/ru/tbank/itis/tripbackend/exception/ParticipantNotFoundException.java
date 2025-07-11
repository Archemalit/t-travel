package ru.tbank.itis.tripbackend.exception;

public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException(Long tripId, Long userId) {
        super("Участник с ID " + userId + " не найден в поездке с ID " + tripId);
    }
}