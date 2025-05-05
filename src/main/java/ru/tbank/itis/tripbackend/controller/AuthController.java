package ru.tbank.itis.tripbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.UserLoginRequest;
import ru.tbank.itis.tripbackend.dto.request.UserRegistrationRequest;
import ru.tbank.itis.tripbackend.dto.response.AuthResponse;
import ru.tbank.itis.tripbackend.service.AuthService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody @Valid UserRegistrationRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public JwtTokenPairDto login(@RequestBody @Valid UserLoginRequest request) {
        return null;
    }

    @PostMapping("/refresh")
    public JwtTokenPairDto refresh() {
        return null;
    }
}
