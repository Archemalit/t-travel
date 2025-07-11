package ru.tbank.itis.tripbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.itis.tripbackend.dictionary.ExpenseCategory;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dto.request.ExpenseParticipantRequest;
import ru.tbank.itis.tripbackend.dto.request.ExpenseRequest;
import ru.tbank.itis.tripbackend.dto.response.ExpenseResponse;
import ru.tbank.itis.tripbackend.exception.*;
import ru.tbank.itis.tripbackend.mapper.ExpenseMapper;
import ru.tbank.itis.tripbackend.model.*;
import ru.tbank.itis.tripbackend.repository.ExpenseRepository;
import ru.tbank.itis.tripbackend.repository.TripParticipantRepository;
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.service.impl.ActualExpenseServiceImpl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActualExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private TripParticipantRepository tripParticipantRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ActualExpenseServiceImpl actualExpenseService;

    private User user;
    private Trip trip;
    private Expense expense;
    private ExpenseRequest expenseRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("79999999999")
                .build();

        trip = Trip.builder()
                .id(1L)
                .title("Test Trip")
                .creator(user)
                .build();

        expense = Expense.builder()
                .id(1L)
                .description("Test Expense")
                .totalAmount(BigDecimal.valueOf(100))
                .trip(trip)
                .paidBy(user)
                .build();

        Set<ExpenseParticipantRequest> participants = new HashSet<>();
        participants.add(new ExpenseParticipantRequest(1L, BigDecimal.valueOf(50)));
        participants.add(new ExpenseParticipantRequest(2L, BigDecimal.valueOf(50)));

        expenseRequest = ExpenseRequest.builder()
                .description("Test Expense")
                .category(ExpenseCategory.valueOf("FOOD"))
                .participants(participants)
                .build();
    }

    @Test
    void getExpenseById_shouldReturnExpense() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 1L, List.of(TripParticipantStatus.ACCEPTED)))
                .thenReturn(true);
        when(expenseMapper.toDto(expense)).thenReturn(new ExpenseResponse());

        ExpenseResponse result = actualExpenseService.getExpenseById(1L, 1L);

        assertThat(result).isNotNull();
        verify(expenseRepository).findById(1L);
    }

    @Test
    void getExpenseById_expenseNotFound_throwsExpenseNotFoundException() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actualExpenseService.getExpenseById(1L, 1L))
                .isInstanceOf(ExpenseNotFoundException.class);
    }

    @Test
    void getExpenseById_userNotParticipant_throwsForbiddenAccessException() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 1L, List.of(TripParticipantStatus.ACCEPTED)))
                .thenReturn(false);

        assertThatThrownBy(() -> actualExpenseService.getExpenseById(1L, 1L))
                .isInstanceOf(ForbiddenAccessException.class);
    }

    @Test
    void getAllExpensesByTrip_shouldReturnExpenses() {
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 1L, List.of(TripParticipantStatus.ACCEPTED)))
                .thenReturn(true);
        when(expenseRepository.findAllByTripId(1L)).thenReturn(List.of(expense));
        when(expenseMapper.toDto(expense)).thenReturn(new ExpenseResponse());

        List<ExpenseResponse> result = actualExpenseService.getAllExpensesByTrip(1L, 1L);

        assertThat(result).hasSize(1);
        verify(expenseRepository).findAllByTripId(1L);
    }

    @Test
    void getAllExpensesByTrip_userNotParticipant_throwsForbiddenAccessException() {
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 1L, List.of(TripParticipantStatus.ACCEPTED)))
                .thenReturn(false);

        assertThatThrownBy(() -> actualExpenseService.getAllExpensesByTrip(1L, 1L))
                .isInstanceOf(ForbiddenAccessException.class);
    }

    @Test
    void createExpense_shouldCreateExpense() {
        TripParticipant participant1 = TripParticipant.builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .status(TripParticipantStatus.ACCEPTED)
                .build();

        TripParticipant participant2 = TripParticipant.builder()
                .id(2L)
                .user(User.builder().id(2L).build())
                .status(TripParticipantStatus.ACCEPTED)
                .build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 1L, List.of(TripParticipantStatus.ACCEPTED)))
                .thenReturn(true);
        when(tripParticipantRepository.findByTripIdAndUserIdAndStatus(eq(1L), eq(1L), eq(TripParticipantStatus.ACCEPTED)))
                .thenReturn(Optional.of(participant1));
        when(tripParticipantRepository.findByTripIdAndUserIdAndStatus(eq(1L), eq(2L), eq(TripParticipantStatus.ACCEPTED)))
                .thenReturn(Optional.of(participant2));

        Expense savedExpense = Expense.builder()
                .id(1L)
                .description("Test Expense")
                .totalAmount(BigDecimal.valueOf(100))
                .trip(trip)
                .paidBy(user)
                .build();

        when(expenseMapper.toEntity(any(ExpenseRequest.class), eq(trip), eq(user)))
                .thenReturn(savedExpense);
        when(expenseMapper.toDto(savedExpense)).thenReturn(new ExpenseResponse());
        when(expenseRepository.save(savedExpense)).thenReturn(savedExpense);

        ExpenseResponse result = actualExpenseService.createExpense(user, 1L, expenseRequest);

        assertThat(result).isNotNull();
        verify(expenseRepository).save(savedExpense);
    }

    @Test
    void createExpense_tripNotFound_throwsTripNotFoundException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actualExpenseService.createExpense(user, 1L, expenseRequest))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void createExpense_userNotParticipant_throwsForbiddenAccessException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 1L, List.of(TripParticipantStatus.ACCEPTED)))
                .thenReturn(false);

        assertThatThrownBy(() -> actualExpenseService.createExpense(user, 1L, expenseRequest))
                .isInstanceOf(ForbiddenAccessException.class);
    }

    @Test
    void createExpense_participantNotFound_throwsParticipantNotFoundException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(1L, 1L, List.of(TripParticipantStatus.ACCEPTED)))
                .thenReturn(true);

        TripParticipant participant1 = TripParticipant.builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .status(TripParticipantStatus.ACCEPTED)
                .build();

        when(tripParticipantRepository.findByTripIdAndUserIdAndStatus(eq(1L), eq(1L), eq(TripParticipantStatus.ACCEPTED)))
                .thenReturn(Optional.of(participant1));
        when(tripParticipantRepository.findByTripIdAndUserIdAndStatus(eq(1L), eq(2L), eq(TripParticipantStatus.ACCEPTED)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> actualExpenseService.createExpense(user, 1L, expenseRequest))
                .isInstanceOf(ParticipantNotFoundException.class);
    }

    @Test
    void deleteExpense_shouldDeleteExpense() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        actualExpenseService.deleteExpense(1L, 1L);

        verify(expenseRepository).deleteById(1L);
    }

    @Test
    void deleteExpense_expenseNotFound_throwsExpenseNotFoundException() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actualExpenseService.deleteExpense(1L, 1L))
                .isInstanceOf(ExpenseNotFoundException.class);
    }

    @Test
    void deleteExpense_userNotCreator_throwsForbiddenAccessException() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThatThrownBy(() -> actualExpenseService.deleteExpense(2L, 1L))
                .isInstanceOf(ForbiddenAccessException.class);
    }
}