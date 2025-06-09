package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dto.PlannedExpenseDto;
import ru.tbank.itis.tripbackend.dto.request.PlannedExpenseRequest;
import ru.tbank.itis.tripbackend.dto.response.PlannedExpenseResponse;
import ru.tbank.itis.tripbackend.exception.ForbiddenAccessException;
import ru.tbank.itis.tripbackend.exception.PlannedExpenseException;
import ru.tbank.itis.tripbackend.exception.PlannedExpenseNotFoundException;
import ru.tbank.itis.tripbackend.exception.TripNotFoundException;
import ru.tbank.itis.tripbackend.mapper.PlannedExpenseMapper;
import ru.tbank.itis.tripbackend.model.PlannedExpense;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.repository.PlannedExpenseRepository;
import ru.tbank.itis.tripbackend.repository.TripParticipantRepository;
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.service.PlannedExpenseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.DoubleStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlannedExpanseServiceImpl implements PlannedExpenseService {

    private final PlannedExpenseRepository plannedExpenseRepository;
    private final TripRepository tripRepository;
    private final TripParticipantRepository tripParticipantRepository;

    private final PlannedExpenseMapper plannedExpenseMapper;

//    @Override
//    public PlannedExpenseResponse getExpenseById(Long tripId, Long userId, Long expenseId) {
//        if (!tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn
//                (tripId, userId, List.of(TripParticipantStatus.ACCEPTED))) {
//            throw new ForbiddenAccessException("Чтобы посмотреть запланированный расход, вам нужно быть участником поездки");
//        }
//            PlannedExpense expense = plannedExpenseRepository.findById(expenseId)
//                .orElseThrow(() -> new PlannedExpenseNotFoundException(expenseId));
//
//        return plannedExpenseMapper.mapExpenseToExpenseDto(expense);
//    }

    @Override
    public List<PlannedExpenseResponse> getAllExpensesByTripId(Long tripId, Long userId) {
        if (!tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn
                (tripId, userId, List.of(TripParticipantStatus.ACCEPTED))) {
            throw new ForbiddenAccessException("Чтобы посмотреть запланированный расход, вам нужно быть участником поездки");
        }

        return plannedExpenseRepository.findAllByTripId(tripId).stream()
                .map(plannedExpenseMapper::mapExpenseToExpenseDto)
                .toList();
    }

    @Override
    public PlannedExpenseResponse createExpense(Long tripId, Long userId, PlannedExpenseRequest expenseDto) {
        if (!tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn
                (tripId, userId, List.of(TripParticipantStatus.ACCEPTED))) {
            throw new ForbiddenAccessException("Чтобы создать запланированный расход, вам нужно быть участником поездки");
        }
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        PlannedExpense expense = plannedExpenseMapper.mapExpenseDtoToExpense(expenseDto, trip);
        if (creationIsAvailable(tripId, expenseDto)) {
            log.info("{} Добавление расхода доступно", LocalDateTime.now());
            plannedExpenseRepository.save(expense);
            return plannedExpenseMapper.mapExpenseToExpenseDto(expense);
        } else {
            throw new PlannedExpenseException("Добавление расхода недоступно");
        }
    }

//    @Override
//    public PlannedExpenseDto updateExpense(Long tripId, Long userId, Long expenseId, PlannedExpenseRequest expenseDto) {
//        if(!plannedExpenseRepository.existsById(expenseId)) {
//            throw new PlannedExpenseNotFoundException(expenseId);
//        } else if (!tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn
//                (tripId, userId, List.of(TripParticipantStatus.ACCEPTED))) {
//            throw new ForbiddenAccessException("Чтобы отредактировать запланированный расход, вам нужно быть участником поездки");
//        }
//
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new TripNotFoundException(tripId));
//        PlannedExpense oldExpense = plannedExpenseRepository.findById(expenseId)
//                .orElseThrow(() -> new PlannedExpenseNotFoundException(expenseId));
//        plannedExpenseRepository.delete(oldExpense);
//        expenseDto.setId(expenseId);
//        PlannedExpense newExpense = plannedExpenseMapper.mapExpenseDtoToExpense(expenseDto, trip);
//        PlannedExpense updatedExpense = plannedExpenseRepository.save(newExpense);
//
//        if(plannedExpenseRepository.existsById(updatedExpense.getId())) {
//            log.info("{} Расход успешно отредактирован", LocalDateTime.now());
//            return plannedExpenseMapper.mapExpenseToExpenseDto(updatedExpense);
//        } else {
//            throw new PlannedExpenseException("Ошибка при редактировании расхода");
//        }
//    }

    @Override
    public void deleteExpense(Long userId, Long expenseId) {
        PlannedExpense plannedExpense = plannedExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new PlannedExpenseNotFoundException(expenseId));
        if(!plannedExpenseRepository.existsById(expenseId)) {
            throw new PlannedExpenseNotFoundException(expenseId);
        } else if (!tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn
                (plannedExpense.getTrip().getId(), userId, List.of(TripParticipantStatus.ACCEPTED))) {
            throw new ForbiddenAccessException("Чтобы удалить запланированный расход, вам нужно быть участником поездки");
        }

        plannedExpenseRepository.deleteById(expenseId);
        log.info("{} Расход с id: {} успешно удален", LocalDateTime.now(), expenseId);
    }


    private boolean creationIsAvailable(Long tripId, PlannedExpenseRequest expenseDto) {
        Double tripBudget = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId)).getTotalBudget();
        Double expensesSum = plannedExpenseRepository.findAllByTripId(tripId).stream()
                .map(PlannedExpense::getAmount)
                .flatMapToDouble(DoubleStream::of)
                .sum();

        if ((expensesSum + expenseDto.getAmount()) <= tripBudget) {
            return true;
        } else {
            throw new PlannedExpenseException("Сумма расходов превышает бюджет поездки");
        }
    }
}
