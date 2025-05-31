package ru.tbank.itis.tripbackend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.tbank.itis.tripbackend.service.RedisRefreshTokenService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RedisRefreshTokenServiceImpl implements RedisRefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    public void save(String refreshToken, String phone, LocalDateTime expiredAt) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, phone, Duration.between(LocalDateTime.now(), expiredAt));
    }

    public Optional<String> findByToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void delete(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.delete(key);
    }
}
