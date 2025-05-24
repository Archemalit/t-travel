package ru.tbank.itis.tripbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Пара JWT-токенов: access и refresh")
public record JwtTokenPairDto(
        @Schema(description = "Access токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxxxx")
        String accessToken,

        @Schema(description = "Refresh токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refreshxxxxx")
        String refreshToken
) {}
