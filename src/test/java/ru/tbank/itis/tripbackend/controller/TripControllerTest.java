package ru.tbank.itis.tripbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import ru.tbank.itis.tripbackend.dto.request.TripRequest;
import ru.tbank.itis.tripbackend.dto.response.TripResponse;
import ru.tbank.itis.tripbackend.exception.TripNotFoundException;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.TripService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripController.class)
@ExtendWith(MockitoExtension.class)
class TripControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TripService tripService;
    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl userDetails;
    private TripRequest tripRequest;
    private TripResponse tripResponse;

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
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(7);

        tripRequest = TripRequest.builder()
                .title("Test Trip")
                .startDate(start)
                .endDate(end)
                .totalBudget(500.0)
                .build();

        tripResponse = TripResponse.builder()
                .id(1L)
                .title("Test Trip")
                .startDate(start)
                .endDate(end)
                .totalBudget(500.0)
                .build();
    }

    @Test
    @DisplayName("GET /trips — получение всех поездок пользователя — возвращает список")
    void getAllTrips_shouldReturnListOfTrips() throws Exception {
        when(tripService.getAllTripsByUserId(1L, false)).thenReturn(List.of(tripResponse));

        mockMvc.perform(get("/api/v1/trips")
                        .param("onlyCreator", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Trip"));

        verify(tripService).getAllTripsByUserId(1L, false);
    }


    @Test
    @DisplayName("GET /trips/{id} — поездка существует — возвращает данные")
    void getTripById_whenTripExists_shouldReturnTrip() throws Exception {
        when(tripService.getTripById(1L, 1L)).thenReturn(tripResponse);

        mockMvc.perform(get("/api/v1/trips/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Trip"));

        verify(tripService).getTripById(1L, 1L);
    }

    @Test
    @DisplayName("GET /trips/{id} — поездка не найдена — выбрасывает TripNotFoundException")
    void getTripById_whenTripNotFound_shouldThrowTripNotFoundException() throws Exception {
        doThrow(new TripNotFoundException(1L)).when(tripService).getTripById(1L, 1L);

        mockMvc.perform(get("/api/v1/trips/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tripService).getTripById(1L, 1L);
    }

    @Test
    @DisplayName("POST /trips — успешно создана новая поездка — возвращает CREATED")
    void createTrip_shouldCreateNewTrip() throws Exception {
        when(tripService.createTrip(any(TripRequest.class), any(User.class))).thenReturn(tripResponse);

        mockMvc.perform(post("/api/v1/trips")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(tripRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Trip"));

        verify(tripService).createTrip(any(TripRequest.class), any(User.class));
    }

    @Test
    @DisplayName("PUT /trips/{id} — обновление поездки — успешно обновлено")
    void updateTrip_shouldUpdateTrip() throws Exception {
        when(tripService.updateTrip(eq(1L), any(TripRequest.class), eq(1L))).thenReturn(tripResponse);

        mockMvc.perform(put("/api/v1/trips/1")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(tripRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Trip"));

        verify(tripService).updateTrip(eq(1L), any(TripRequest.class), eq(1L));
    }

    @Test
    @DisplayName("PUT /trips/{id} — поездка не найдена — выбрасывает TripNotFoundException")
    void updateTrip_whenTripNotFound_shouldThrowTripNotFoundException() throws Exception {
        doThrow(new TripNotFoundException(1L)).when(tripService).updateTrip(eq(1L), any(TripRequest.class), eq(1L));

        mockMvc.perform(put("/api/v1/trips/1")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(tripRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tripService).updateTrip(eq(1L), any(TripRequest.class), eq(1L));
    }

    @Test
    @DisplayName("DELETE /trips/{id} — удаление поездки — успешно удалено")
    void deleteTrip_shouldDeleteTrip() throws Exception {
        mockMvc.perform(delete("/api/v1/trips/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(tripService).deleteTrip(1L, 1L);
    }

    @Test
    @DisplayName("DELETE /trips/{id} — поездка не найдена — выбрасывает TripNotFoundException")
    void deleteTrip_whenTripNotFound_shouldThrowTripNotFoundException() throws Exception {
        doThrow(new TripNotFoundException(1L)).when(tripService).deleteTrip(1L, 1L);

        mockMvc.perform(delete("/api/v1/trips/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tripService).deleteTrip(1L, 1L);
    }
}