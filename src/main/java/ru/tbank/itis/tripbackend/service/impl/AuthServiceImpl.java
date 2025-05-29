package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.security.jwt.service.JwtService;
import ru.tbank.itis.tripbackend.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;

    @Override
    public SimpleResponse logout(UserDetailsImpl userDetails) {
        jwtService.invalidateToken(userDetails.getUsername());
        
        SecurityContextHolder.clearContext();
        
        return SimpleResponse.builder()
                .success(true)
                .message("Вы успешно вышли из аккаунта!")
                .build();
    }
}