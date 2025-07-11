package ru.tbank.itis.tripbackend.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.dto.UserDto;
import ru.tbank.itis.tripbackend.dto.request.UserRegistrationRequest;
import ru.tbank.itis.tripbackend.dto.request.UserUpdateProfileRequest;
import ru.tbank.itis.tripbackend.dto.response.UserExistsResponse;
import ru.tbank.itis.tripbackend.dto.response.UserProfileResponse;
import ru.tbank.itis.tripbackend.model.User;

import java.util.Set;

public interface UserService {
    JwtTokenPairDto register(UserRegistrationRequest request);
    UserExistsResponse doesUserExistByPhoneNumber(String phoneNumber);
    UserProfileResponse getUserProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UserUpdateProfileRequest request);
}
