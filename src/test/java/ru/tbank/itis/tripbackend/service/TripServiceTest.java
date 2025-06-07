package ru.tbank.itis.tripbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dictionary.UserRole;
import ru.tbank.itis.tripbackend.dto.request.TripRequest;
import ru.tbank.itis.tripbackend.dto.response.TripResponse;
import ru.tbank.itis.tripbackend.exception.ForbiddenAccessException;
import ru.tbank.itis.tripbackend.exception.TripNotFoundException;
import ru.tbank.itis.tripbackend.exception.ValidationException;
import ru.tbank.itis.tripbackend.mapper.TripMapper;
import ru.tbank.itis.tripbackend.mapper.TripMapperImpl;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.TripParticipant;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.service.impl.TripServiceImpl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {
    @InjectMocks
    private TripServiceImpl tripService;
    @Mock
    private TripRepository tripRepository;
    @Spy
    private TripMapper tripMapper = new TripMapperImpl();

    private User mockUser;
    private Trip mockTrip;
    private TripRequest mockTripRequest;
    private TripResponse mockTripResponse;
    private TripParticipant mockParticipant;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .firstName("Name")
                .lastName("Surname")
                .phoneNumber("79999999999")
                .password("123")
                .role(UserRole.ADMIN)
                .build();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(5);
        Double budget = 1000.0;

        mockTrip = Trip.builder()
                .id(1L)
                .title("Test Trip")
                .startDate(startDate)
                .endDate(endDate)
                .totalBudget(budget)
                .creator(mockUser)
                .status(ForTripAndInvitationStatus.ACTIVE)
                .build();

        mockParticipant = TripParticipant.builder()
                .id(1L)
                .status(TripParticipantStatus.ACCEPTED)
                .trip(mockTrip)
                .user(mockUser)
                .build();

        Set<TripParticipant> participants = new HashSet<>(Set.of(mockParticipant));

        mockTrip.setParticipants(participants);
        mockTripRequest = TripRequest.builder()
                .title("Updated Trip")
                .startDate(startDate)
                .endDate(endDate)
                .totalBudget(2000.0)
                .build();
        mockTripResponse = TripResponse.builder()
                .id(1L)
                .title("Test Trip")
                .startDate(startDate)
                .endDate(endDate)
                .totalBudget(budget)
                .status(ForTripAndInvitationStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Получение поездок пользователя — где он участник — возвращает список")
    void getAllTripsByUserId_withoutOnlyCreator_shouldReturnParticipantTrips() {
        when(tripRepository.findByParticipantsUserId(1L)).thenReturn(List.of(mockTrip));

        List<TripResponse> result = tripService.getAllTripsByUserId(1L, false, false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Trip");
        verify(tripRepository).findByParticipantsUserId(1L);
    }

    @Test
    @DisplayName("Получение поездки по ID — поездка существует и пользователь участник — возвращает данные")
    void getTripById_whenTripExistsAndUserIsParticipant_shouldReturnTrip() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(mockTrip));

        TripResponse result = tripService.getTripById(1L, mockUser.getId());

        assertThat(result).isEqualTo(mockTripResponse);
    }

    @Test
    @DisplayName("Получение поездки по ID — поездка не найдена — выбрасывает TripNotFoundException")
    void getTripById_whenTripNotFound_shouldThrowTripNotFoundException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.getTripById(1L, 1L))
                .isInstanceOf(TripNotFoundException.class);
    }

    @Test
    @DisplayName("Получение поездки по ID — пользователь не участник — выбрасывает ForbiddenAccessException")
    void getTripById_whenUserIsNotParticipant_shouldThrowForbiddenAccessException() {
        Trip tripWithoutUser = new Trip()
                .setId(1L)
                .setParticipants(new HashSet<>());
        when(tripRepository.findById(1L)).thenReturn(Optional.of(tripWithoutUser));

        assertThatThrownBy(() -> tripService.getTripById(1L, 1L))
                .isInstanceOf(ForbiddenAccessException.class);
    }

    @Test
    @DisplayName("Создание поездки — успешно создано с текущим пользователем как создателем и участником")
    void createTrip_shouldCreateNewTripWithCurrentUserAsCreatorAndParticipant() {
        when(tripMapper.toEntity(mockTripRequest)).thenReturn(mockTrip);
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tripMapper.toDto(any(Trip.class))).thenReturn(mockTripResponse);

        TripResponse result = tripService.createTrip(mockTripRequest, mockUser);

        assertThat(result.getTitle()).isEqualTo("Test Trip");
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    @DisplayName("Обновление поездки — пользователь создатель — успешно обновлено")
    void updateTrip_whenUserIsCreator_shouldUpdateTripSuccessfully() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(mockTrip));
        when(tripRepository.save(mockTrip)).thenReturn(mockTrip);

        TripResponse result = tripService.updateTrip(1L, mockTripRequest, 1L);

        assertThat(result.getTitle()).isEqualTo("Updated Trip");
        verify(tripRepository).save(mockTrip);
    }

    @Test
    @DisplayName("Обновление поездки — пользователь не создатель — выбрасывает ForbiddenAccessException")
    void updateTrip_whenUserIsNotCreator_shouldThrowForbiddenAccessException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(mockTrip));

        assertThatThrownBy(() -> tripService.updateTrip(1L, mockTripRequest, 999L))
                .isInstanceOf(ForbiddenAccessException.class);
    }

    @Test
    @DisplayName("Обновление поездки — бюджет отрицательный — выбрасывает ValidationException")
    void updateTrip_whenBudgetIsNegative_shouldThrowValidationException() {
        TripRequest invalidRequest = new TripRequest();
        invalidRequest.setTotalBudget(-100.0);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(mockTrip));

        assertThatThrownBy(() -> tripService.updateTrip(1L, invalidRequest, mockUser.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Бюджет не может быть отрицательным");
    }

    @Test
    @DisplayName("Удаление поездки — пользователь создатель — успешно удалено")
    void deleteTrip_whenUserIsCreator_shouldDeleteTripSuccessfully() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(mockTrip));

        tripService.deleteTrip(1L, 1L);

        verify(tripRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Удаление поездки — пользователь не создатель — выбрасывает ForbiddenAccessException")
    void deleteTrip_whenUserIsNotCreator_shouldThrowForbiddenAccessException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(mockTrip));

        assertThatThrownBy(() -> tripService.deleteTrip(1L, 999L))
                .isInstanceOf(ForbiddenAccessException.class);
    }

    @Test
    @DisplayName("Удаление поездки — поездка не найдена — выбрасывает TripNotFoundException")
    void deleteTrip_whenTripDoesNotExist_shouldThrowTripNotFoundException() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.deleteTrip(1L, 1L))
                .isInstanceOf(TripNotFoundException.class);
    }
}
