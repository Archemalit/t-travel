package ru.tbank.itis.tripbackend.exception;

public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException(Long invitationId) {
        super("Приглашение с ID " + invitationId + " не найдено");
    }
}