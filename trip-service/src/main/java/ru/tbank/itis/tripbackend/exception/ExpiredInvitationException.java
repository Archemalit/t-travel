package ru.tbank.itis.tripbackend.exception;

import org.springframework.security.core.AuthenticationException;

public class ExpiredInvitationException extends AuthenticationException {
    public ExpiredInvitationException(String message) {
        super(message);
    }
}