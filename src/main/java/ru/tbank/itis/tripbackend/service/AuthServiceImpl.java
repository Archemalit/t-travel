package ru.tbank.itis.tripbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.itis.tripbackend.dictonary.UserRole;
import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.RefreshTokenRequest;
import ru.tbank.itis.tripbackend.dto.request.UserLoginRequest;
import ru.tbank.itis.tripbackend.dto.request.UserRegistrationRequest;
import ru.tbank.itis.tripbackend.dto.response.AuthResponse;
import ru.tbank.itis.tripbackend.exception.PhoneNumberAlreadyTakenException;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.RefreshTokenRepository;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.security.jwt.service.JwtService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    @Override
    public AuthResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new PhoneNumberAlreadyTakenException("Этот номер телефона уже используется");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .password(encoder.encode(request.password()))
                .role(UserRole.USER)
                .build();
        userRepository.save(user);

        JwtTokenPairDto jwtPair = jwtService.getTokenPair(request.phoneNumber());
        return new AuthResponse(jwtPair.accessToken(), jwtPair.refreshToken());
    }

    @Override
    public SimpleResponse logout() {
        refreshTokenRepository.deleteByToken()
    }

//    @Override
//    public AuthResponse login(UserLoginRequest request) {
//        User user = userRepository.findUserByPhoneNumber(request.phoneNumber())
//                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
//
//        if (!encoder.matches(request.password(), user.getPassword())) {
//            throw new IllegalArgumentException("Неверный пароль");
//        }
//
//        JwtTokenPairDto jwtPair = jwtService.getTokenPair(request.phoneNumber());
//        return new AuthResponse(jwtPair.accessToken(), jwtPair.refreshToken());
//    }
//
//    @Override
//    public AuthResponse refresh(RefreshTokenRequest request) {
//        return null;
//    }
}
