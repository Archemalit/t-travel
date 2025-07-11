package ru.tbank.itis.tripbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tbank.itis.tripbackend.dictionary.UserRole;
import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.dto.request.UserRegistrationRequest;
import ru.tbank.itis.tripbackend.dto.request.UserUpdateProfileRequest;
import ru.tbank.itis.tripbackend.dto.response.UserExistsResponse;
import ru.tbank.itis.tripbackend.dto.response.UserProfileResponse;
import ru.tbank.itis.tripbackend.exception.PhoneNumberAlreadyTakenException;
import ru.tbank.itis.tripbackend.exception.UserNotFoundException;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.security.jwt.service.JwtService;
import ru.tbank.itis.tripbackend.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("79999999999")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();
    }

    @Test
    void register_shouldRegisterNewUserAndReturnTokens() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John",
                "Doe",
                "79999999999",
                "password123",
                "password123"
        );

        when(userRepository.existsByPhoneNumber("79999999999")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.getTokenPair("79999999999")).thenReturn(
                new JwtTokenPairDto("accessToken", "refreshToken")
        );

        JwtTokenPairDto result = userService.register(request);

        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void register_whenPhoneNumberTaken_shouldThrowException() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John",
                "Doe",
                "79999999999",
                "password123",
                "password123"
        );

        when(userRepository.existsByPhoneNumber("79999999999")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(PhoneNumberAlreadyTakenException.class)
                .hasMessageContaining("Пользователь с таким номером телефона уже существует");
    }

    @Test
    void doesUserExistByPhoneNumber_whenUserExists_shouldReturnTrue() {
        when(userRepository.existsByPhoneNumber("79999999999")).thenReturn(true);

        UserExistsResponse response = userService.doesUserExistByPhoneNumber("79999999999");

        assertThat(response.isExists()).isTrue();
    }

    @Test
    void doesUserExistByPhoneNumber_whenUserNotExists_shouldReturnFalse() {
        when(userRepository.existsByPhoneNumber("79999999999")).thenReturn(false);

        UserExistsResponse response = userService.doesUserExistByPhoneNumber("79999999999");

        assertThat(response.isExists()).isFalse();
    }

    @Test
    void getUserProfile_shouldReturnUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserProfileResponse response = userService.getUserProfile(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getPhoneNumber()).isEqualTo("79999999999");
        assertThat(response.getRole()).isEqualTo("USER");
    }

    @Test
    void getUserProfile_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с ID 1 не найден");
    }

    @Test
    void getUserProfile_shouldMapUserRoleCorrectly() {
        testUser.setRole(UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserProfileResponse response = userService.getUserProfile(1L);

        assertThat(response.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void updateProfile_shouldUpdateAndReturnProfile() {
        UserUpdateProfileRequest request = new UserUpdateProfileRequest();
        request.setFirstName("UpdatedName");
        request.setLastName("UpdatedLastName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileResponse response = userService.updateProfile(1L, request);

        assertThat(response.getFirstName()).isEqualTo("UpdatedName");
        assertThat(response.getLastName()).isEqualTo("UpdatedLastName");
        assertThat(testUser.getFirstName()).isEqualTo("UpdatedName");
        assertThat(testUser.getLastName()).isEqualTo("UpdatedLastName");
    }

    @Test
    void updateProfile_whenUserNotFound_shouldThrowException() {
        UserUpdateProfileRequest request = new UserUpdateProfileRequest();
        request.setFirstName("UpdatedName");
        request.setLastName("UpdatedLastName");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(1L, request))
                .isInstanceOf(UserNotFoundException.class);
    }
}