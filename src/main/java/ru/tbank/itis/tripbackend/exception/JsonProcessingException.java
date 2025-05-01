package ru.tbank.itis.tripbackend.exception;

public class JsonProcessingException extends RuntimeException {
    public JsonProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}