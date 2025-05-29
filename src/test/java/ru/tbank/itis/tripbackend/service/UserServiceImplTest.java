package ru.tbank.itis.tripbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.itis.tripbackend.dictionary.UserRole;
import ru.tbank.itis.tripbackend.dto.request.UserUpdateProfileRequest;
import ru.tbank.itis.tripbackend.dto.response.UserProfileResponse;
import ru.tbank.itis.tripbackend.exception.UserNotFoundException;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

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
                .password("password")
                .role(UserRole.USER)
                .build();
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