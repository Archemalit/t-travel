package ru.tbank.itis.tripbackend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final String REFRESH_TOKEN_BLACKLIST_PREFIX = "blacklist:";

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

//    public void block(String refreshToken, String username, LocalDateTime expiredAt) {
//        String key = REFRESH_TOKEN_BLACKLIST_PREFIX + refreshToken;
//        redisTemplate.opsForValue().set(key, username, Duration.between(LocalDateTime.now(), expiredAt));
//    }
//
//    public boolean inBlocked(String refreshToken) {
//        String key = REFRESH_TOKEN_BLACKLIST_PREFIX + refreshToken;
//        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
//    }
}
