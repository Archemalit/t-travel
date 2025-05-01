package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.request.UserRegistrationRequest;
import ru.tbank.itis.tripbackend.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(UserRegistrationRequest request);
//    SimpleResponse logout();
//    AuthResponse login(UserLoginRequest request);
//    AuthResponse refresh(RefreshTokenRequest request);
}
