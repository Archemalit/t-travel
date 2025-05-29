package ru.tbank.itis.tripbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.itis.tripbackend.dictionary.UserRole;
import ru.tbank.itis.tripbackend.dto.request.UserUpdateProfileRequest;
import ru.tbank.itis.tripbackend.dto.response.UserProfileResponse;
import ru.tbank.itis.tripbackend.exception.UserNotFoundException;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        User mockUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("79999999999")
                .password("password")
                .role(UserRole.USER)
                .build();

        userDetails = new UserDetailsImpl(mockUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getCurrentUserProfile_shouldReturnUserProfile() throws Exception {
        UserProfileResponse mockResponse = UserProfileResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("79999999999")
                .role("USER")
                .build();

        when(userService.getUserProfile(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("79999999999"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void getCurrentUserProfile_whenUserNotFound_shouldReturnNotFound() throws Exception {
        when(userService.getUserProfile(1L))
                .thenThrow(new UserNotFoundException(1L));

        mockMvc.perform(get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfile_shouldUpdateAndReturnProfile() throws Exception {
        UserUpdateProfileRequest request = new UserUpdateProfileRequest();
        request.setFirstName("UpdatedName");
        request.setLastName("UpdatedLastName");

        UserProfileResponse mockResponse = UserProfileResponse.builder()
                .id(1L)
                .firstName("UpdatedName")
                .lastName("UpdatedLastName")
                .phoneNumber("79999999999")
                .role("USER")
                .build();

        when(userService.updateProfile(eq(1L), any(UserUpdateProfileRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(patch("/api/users/me")
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedName"))
                .andExpect(jsonPath("$.lastName").value("UpdatedLastName"));
    }

    @Test
    void updateProfile_whenInvalidData_shouldReturnBadRequest() throws Exception {
        UserUpdateProfileRequest invalidRequest = new UserUpdateProfileRequest();
        invalidRequest.setFirstName("");
        invalidRequest.setLastName("");

        mockMvc.perform(patch("/api/users/me")
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}