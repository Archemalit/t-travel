package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.dto.request.UserRegistrationRequest;
import ru.tbank.itis.tripbackend.dto.request.UserUpdateProfileRequest;
import ru.tbank.itis.tripbackend.dto.response.UserExistsResponse;
import ru.tbank.itis.tripbackend.dto.response.UserProfileResponse;

public interface UserService {
    JwtTokenPairDto register(UserRegistrationRequest request);
    UserExistsResponse doesUserExistByPhoneNumber(String phoneNumber);
    UserProfileResponse getUserProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UserUpdateProfileRequest request);
//    SimpleResponse logout();
//    AuthResponse login(UserLoginRequest request);
//    AuthResponse refresh(RefreshTokenRequest request);
}
