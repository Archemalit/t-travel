package ru.tbank.itis.tripbackend.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
