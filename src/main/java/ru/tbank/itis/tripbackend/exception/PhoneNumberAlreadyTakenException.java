package ru.tbank.itis.tripbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class PhoneNumberAlreadyTakenException extends RuntimeException {

    private final String field;
    private final Object rejectedValue;
    private final String message;

    public PhoneNumberAlreadyTakenException(String field, Object rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    public PhoneNumberAlreadyTakenException(String message) {
        super(message);
        this.field = null;
        this.rejectedValue = null;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}