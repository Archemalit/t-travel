package ru.tbank.itis.tripbackend.security.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.service.RedisRefreshTokenService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class JwtService {

    private final static String AUTH_HEADER = "Authorization";
    private final static String AUTH_HEADER_PREFIX = "Bearer ";
    private static final String PHONE_NUMBER_CLAIM = "phoneNumber";
    private static final String TYPE_CLAIM = "type";
    private static final String ACCESS_TYPE_CLAIM = "access";
    private static final String REFRESH_TYPE_CLAIM = "refresh";

    @Value("${jwt.access-token.ttl}")
    private Long accessTokenTtl;

    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final UserRepository userRepository;
    private final RedisRefreshTokenService redisRefreshTokenService;

    public JwtTokenPairDto getTokenPair(String phoneNumber) {
        return new JwtTokenPairDto(
                createAccessToken(phoneNumber),
                createRefreshToken(phoneNumber));
    }

    private String createAccessToken(String phoneNumber) {
        return JWT.create()
                .withExpiresAt(new Date(
                        new Date().getTime() + accessTokenTtl))
                .withClaim(PHONE_NUMBER_CLAIM, phoneNumber)
                .withClaim(TYPE_CLAIM, ACCESS_TYPE_CLAIM)
                .sign(algorithm);
    }

    private String createRefreshToken(String phoneNumber) {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(10);
        Date expiresAtDate = Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant());

        String refreshToken = JWT.create()
                .withExpiresAt(expiresAtDate)
                .withClaim(PHONE_NUMBER_CLAIM, phoneNumber)
                .withClaim(TYPE_CLAIM, REFRESH_TYPE_CLAIM)
                .sign(algorithm);
        redisRefreshTokenService.save(refreshToken, phoneNumber, expiresAt);

        return refreshToken;
    }

    public String getRawToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);

        if (header != null && header.startsWith(AUTH_HEADER_PREFIX)) {
            return header.substring(AUTH_HEADER_PREFIX.length());
        }

        throw new BadCredentialsException("Вы не передали access-токен!");
    }

    public String getPhoneNumber(String token) {
        return Optional.of(jwtVerifier.verify(token))
                .map(jwt -> jwt.getClaim(PHONE_NUMBER_CLAIM))
                .map(Claim::asString)
                .orElse(null);
    }

    public boolean isAccessToken(String token) {
        return Optional.of(jwtVerifier.verify(token))
                .map(jwt -> jwt.getClaim(TYPE_CLAIM))
                .filter(claim -> REFRESH_TYPE_CLAIM.equals(claim.asString()))
                .isPresent();
    }
}

