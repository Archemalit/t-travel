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
import ru.tbank.itis.tripbackend.dictionary.ExpenseCategory;
import ru.tbank.itis.tripbackend.dictionary.UserRole;
import ru.tbank.itis.tripbackend.dto.request.ExpenseParticipantRequest;
import ru.tbank.itis.tripbackend.dto.request.ExpenseRequest;
import ru.tbank.itis.tripbackend.dto.response.ExpenseResponse;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.ActualExpenseService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActualExpenseController.class)
@ExtendWith(MockitoExtension.class)
class ActualExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActualExpenseService actualExpenseService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl userDetails;
    private ExpenseRequest expenseRequest;
    private ExpenseResponse expenseResponse;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("79999999999")
                .role(UserRole.USER)
                .build();

        userDetails = new UserDetailsImpl(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Set<ExpenseParticipantRequest> participants = new HashSet<>();
        participants.add(new ExpenseParticipantRequest(1L, BigDecimal.valueOf(50)));
        participants.add(new ExpenseParticipantRequest(2L, BigDecimal.valueOf(50)));

        expenseRequest = ExpenseRequest.builder()
                .description("Test Expense")
                .category(ExpenseCategory.valueOf("FOOD"))
                .participants(participants)
                .build();

        expenseResponse = ExpenseResponse.builder()
                .id(1L)
                .description("Test Expense")
                .totalAmount(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    void getExpenseById_shouldReturnExpense() throws Exception {
        when(actualExpenseService.getExpenseById(1L, 1L)).thenReturn(expenseResponse);

        mockMvc.perform(get("/api/v1/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(actualExpenseService).getExpenseById(1L, 1L);
    }

    @Test
    void getAllExpensesByTrip_shouldReturnExpenses() throws Exception {
        when(actualExpenseService.getAllExpensesByTrip(1L, 1L)).thenReturn(List.of(expenseResponse));

        mockMvc.perform(get("/api/v1/expenses/trip/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(actualExpenseService).getAllExpensesByTrip(1L, 1L);
    }

    @Test
    void createExpense_shouldCreateExpense() throws Exception {
        when(actualExpenseService.createExpense(any(User.class), eq(1L), any(ExpenseRequest.class)))
                .thenReturn(expenseResponse);

        mockMvc.perform(post("/api/v1/expenses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(actualExpenseService).createExpense(any(User.class), eq(1L), any(ExpenseRequest.class));
    }

    @Test
    void deleteExpense_shouldDeleteExpense() throws Exception {
        doNothing().when(actualExpenseService).deleteExpense(1L, 1L);

        mockMvc.perform(delete("/api/v1/expenses/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(actualExpenseService).deleteExpense(1L, 1L);
    }
}