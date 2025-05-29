package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;

public interface AuthService {
    SimpleResponse logout(UserDetailsImpl userDetails);
}