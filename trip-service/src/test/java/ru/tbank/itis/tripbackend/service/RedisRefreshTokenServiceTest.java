package ru.tbank.itis.tripbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import ru.tbank.itis.tripbackend.service.impl.RedisRefreshTokenServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisRefreshTokenServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisRefreshTokenServiceImpl redisRefreshTokenService;

    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String PHONE = "79999999999";
    private static final LocalDateTime EXPIRED_AT = LocalDateTime.now().plusDays(1);

    @Test
    void save_shouldSaveRefreshToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisRefreshTokenService.save(REFRESH_TOKEN, PHONE, EXPIRED_AT);

        verify(valueOperations).set(
                eq("refresh_token:" + REFRESH_TOKEN),
                eq(PHONE),
                any(Duration.class)
        );
    }

    @Test
    void findByToken_shouldFindRefreshToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:" + REFRESH_TOKEN)).thenReturn(PHONE);

        Optional<String> result = redisRefreshTokenService.findByToken(REFRESH_TOKEN);

        assertThat(result).isPresent().contains(PHONE);
    }

    @Test
    void findByToken_tokenNotFound_returnsEmptyOptional() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:" + REFRESH_TOKEN)).thenReturn(null);

        Optional<String> result = redisRefreshTokenService.findByToken(REFRESH_TOKEN);

        assertThat(result).isEmpty();
    }

    @Test
    void delete_shouldDeleteRefreshToken() {
        redisRefreshTokenService.delete(REFRESH_TOKEN);

        verify(redisTemplate).delete("refresh_token:" + REFRESH_TOKEN);
    }
}