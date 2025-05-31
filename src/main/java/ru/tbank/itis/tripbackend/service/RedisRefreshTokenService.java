package ru.tbank.itis.tripbackend.service;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RedisRefreshTokenService {
    void save(String refreshToken, String phone, LocalDateTime expiredAt);
    Optional<String> findByToken(String refreshToken);
    void delete(String refreshToken);
}
