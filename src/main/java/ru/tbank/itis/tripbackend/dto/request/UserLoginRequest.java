package ru.tbank.itis.tripbackend.dto.request;

public record UserLoginRequest(
        String phoneNumber,
        String password,
        String repeatPassword
) {}
