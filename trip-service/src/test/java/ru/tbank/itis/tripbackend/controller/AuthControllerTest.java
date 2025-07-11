package ru.tbank.itis.tripbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.dto.request.UserRegistrationRequest;
import ru.tbank.itis.tripbackend.dto.response.UserExistsResponse;
import ru.tbank.itis.tripbackend.service.UserService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserRegistrationRequest registrationRequest;
    private JwtTokenPairDto jwtTokenPairDto;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setValidator(validator)
                .build();

        registrationRequest = UserRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("79999999999")
                .password("password")
                .repeatPassword("password")
                .build();

        jwtTokenPairDto = JwtTokenPairDto.builder()
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .build();
    }

    @Test
    void register_shouldRegisterUser() throws Exception {
        when(userService.register(any(UserRegistrationRequest.class)))
                .thenReturn(jwtTokenPairDto);

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token"));

        verify(userService).register(any(UserRegistrationRequest.class));
    }

    @Test
    void register_withInvalidData_shouldReturnBadRequest() throws Exception {
        UserRegistrationRequest invalidRequest = UserRegistrationRequest.builder()
                .firstName("")
                .lastName("")
                .phoneNumber("invalid")
                .password("")
                .repeatPassword("")
                .build();

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doesUserExistByPhoneNumber_shouldCheckUser() throws Exception {
        when(userService.doesUserExistByPhoneNumber("79999999999"))
                .thenReturn(new UserExistsResponse(true));

        mockMvc.perform(get("/api/v1/check")
                        .param("phone", "79999999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        verify(userService).doesUserExistByPhoneNumber("79999999999");
    }

    @Test
    void doesUserExistByPhoneNumber_withInvalidPhone_shouldReturnFalse() throws Exception {
        when(userService.doesUserExistByPhoneNumber(anyString()))
                .thenReturn(new UserExistsResponse(false));

        mockMvc.perform(get("/api/v1/check")
                        .param("phone", "invalid_number"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }
}