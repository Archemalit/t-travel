package ru.tbank.itis.tripbackend.dto;

public record JwtTokenPairDto(
        String accessToken,
        String refreshToken
) {}
