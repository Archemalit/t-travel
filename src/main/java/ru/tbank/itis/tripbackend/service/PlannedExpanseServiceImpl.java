package ru.tbank.itis.tripbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.itis.tripbackend.dto.PlannedExpenseDto;
import ru.tbank.itis.tripbackend.mapper.PlannedExpenseMapper;
import ru.tbank.itis.tripbackend.model.PlannedExpense;
import ru.tbank.itis.tripbackend.repository.PlannedExpenseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.DoubleStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlannedExpanseServiceImpl implements PlannedExpenseService {

    private final PlannedExpenseRepository plannedExpenseRepository;
    private final PlannedExpenseMapper plannedExpenseMapper;

    private final TripService tripService;

    @Override
    public PlannedExpenseDto getExpenseById(Long id) {
        PlannedExpense expense = plannedExpenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Не найдено расходов с id: " + id));

        return plannedExpenseMapper.mapExpenseToExpenseDto(expense);
    }

    @Override
    public List<PlannedExpenseDto> getAllExpensesByTripId(Long tripId) {
        return plannedExpenseRepository.findAllByTripId(tripId).stream()
                .map(plannedExpenseMapper::mapExpenseToExpenseDto)
                .toList();
    }

    @Override
    public PlannedExpenseDto createExpense(Long tripId, PlannedExpenseDto expenseDto) {
        PlannedExpense expense = plannedExpenseMapper.mapExpenseDtoToExpense(expenseDto);
        if (creationIsAvailable(tripId, expenseDto)) {
            log.info("{} Добавление расхода доступно", LocalDateTime.now());
            expense = plannedExpenseRepository.save(expense);
        } else {
            throw new RuntimeException("Добавление расхода доступно");
        }

        if (plannedExpenseRepository.existsById(expense.getId())) {
            log.info("{} Расход успешно сохранен", LocalDateTime.now());
            return plannedExpenseMapper.mapExpenseToExpenseDto(expense);
        } else {
            throw new RuntimeException("Ошибка при сохранении расхода");
        }

    }

    @Override
    public PlannedExpenseDto updateExpense(Long tripId, Long expenseId, PlannedExpenseDto expenseDto) {
        if(!plannedExpenseRepository.existsById(expenseId)) {
            throw new RuntimeException("Не найдено расходов с id: " + expenseId);
        } else if (plannedExpenseRepository.existsByTripId(tripId)) {
            throw new RuntimeException("Не найдено расходов для поездки с id: " + tripId);
        }

        PlannedExpense oldExpense = plannedExpenseRepository.findById(expenseId).get();
        plannedExpenseRepository.delete(oldExpense);
        expenseDto.setId(expenseId);
        PlannedExpense newExpense = plannedExpenseMapper.mapExpenseDtoToExpense(expenseDto);
        PlannedExpense updatedExpense = plannedExpenseRepository.save(newExpense);

        if(plannedExpenseRepository.existsById(updatedExpense.getId())) {
            log.info("{} Расход успешно отредактирован", LocalDateTime.now());
            return plannedExpenseMapper.mapExpenseToExpenseDto(updatedExpense);
        } else {
            throw new RuntimeException("Ошибка при редактировании расхода");
        }
    }

    @Override
    public void deleteExpense(Long tripId, Long expenseId) {
        if(!plannedExpenseRepository.existsById(expenseId)) {
            throw new RuntimeException("Не найдено расходов с id: " + expenseId);
        } else if (plannedExpenseRepository.existsByTripId(tripId)) {
            throw new RuntimeException("Не найдено расходов для поездки с id: " + tripId);
        }

        plannedExpenseRepository.deleteById(expenseId);
        log.info("{} Расход с id: {} успешно удален", LocalDateTime.now(), expenseId);
    }


    private boolean creationIsAvailable(Long tripId, PlannedExpenseDto expenseDto) {
        Double tripBudget = tripService.getTripById(tripId).getTotalBudget();
        Double expensesSum = plannedExpenseRepository.findAllByTripId(tripId).stream()
                .map(PlannedExpense::getAmount)
                .flatMapToDouble(DoubleStream::of)
                .sum();

        if ((expensesSum + expenseDto.getAmount()) <= tripBudget) {
            return true;
        } else {
            throw new RuntimeException("Сумма расходов превышает бюджет поездки");
        }
    }
}
