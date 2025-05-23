package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.dto.request.UserRegistrationRequest;
import ru.tbank.itis.tripbackend.dto.response.UserExistsResponse;

public interface UserService {
    JwtTokenPairDto register(UserRegistrationRequest request);
    UserExistsResponse doesUserExistByPhoneNumber(String phoneNumber);
//    SimpleResponse logout();
//    AuthResponse login(UserLoginRequest request);
//    AuthResponse refresh(RefreshTokenRequest request);
}
