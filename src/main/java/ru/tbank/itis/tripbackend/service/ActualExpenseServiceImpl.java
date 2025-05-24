package ru.tbank.itis.tripbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.itis.tripbackend.dto.ActualExpenseDto;
import ru.tbank.itis.tripbackend.exception.expense.ExpenseNotFoundException;
import ru.tbank.itis.tripbackend.exception.expense.ExpenseNotFoundForMemberException;
import ru.tbank.itis.tripbackend.exception.expense.ExpenseNotFoundForTripException;
import ru.tbank.itis.tripbackend.mapper.ActualExpenseMapper;
import ru.tbank.itis.tripbackend.model.ActualExpense;
import ru.tbank.itis.tripbackend.repository.ActualExpenseRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActualExpenseServiceImpl implements ActualExpenseService {

    private final ActualExpenseRepository actualExpenseRepository;
    private final ActualExpenseMapper actualExpenseMapper;

    @Override
    public List<ActualExpenseDto> getAllExpensesByTrip(Long tripId) {
        if (actualExpenseRepository.existsByTripId(tripId)) {
            throw new ExpenseNotFoundForTripException(tripId);
        }
        return actualExpenseRepository.findAllByTripId(tripId).stream()
                .map(actualExpenseMapper::mapExpenseToExpenseDto)
                .toList();
    }

    @Override
    public List<ActualExpenseDto> getAllExpensesByTripMember(Long tripId, Long memberId) {
        if (actualExpenseRepository.existsByTripId(tripId)) {
            throw new ExpenseNotFoundForTripException(tripId);
        } else if (!actualExpenseRepository.existsByMemberId(memberId)) {
            throw new ExpenseNotFoundForMemberException(memberId);
        }
        return actualExpenseRepository.findAllByTripId(tripId).stream()
                .filter(expense -> expense.getPaidByUserId().equals(memberId))
                .map(actualExpenseMapper::mapExpenseToExpenseDto)
                .toList();
    }

    @Override
    public ActualExpenseDto createExpense(ActualExpenseDto expenseDto) {
        ActualExpense expense = actualExpenseMapper.mapExpenseDtoToExpense(expenseDto);
        ActualExpense savedExpense = actualExpenseRepository.save(expense);
        if(actualExpenseRepository.existsById(savedExpense.getId())) {
            log.info("{} Расход успешно сохранен", LocalDateTime.now());
            return actualExpenseMapper.mapExpenseToExpenseDto(savedExpense);
        } else {
            throw new RuntimeException("Ошибка при сохранении расхода");
        }
    }

    @Override
    public ActualExpenseDto updateExpense(Long userId, Long tripId, Long expenseId, ActualExpenseDto expenseDto) {
        if(!actualExpenseRepository.existsById(expenseId)) {
            throw new ExpenseNotFoundException(expenseId);
        } else if (!actualExpenseRepository.existsByTripId(tripId)) {
            throw new ExpenseNotFoundForTripException(tripId);
        } else if (!actualExpenseRepository.findById(expenseId).get().getPaidByUserId().equals(userId)) {
            throw new RuntimeException("Не достаточно прав для редактирования расхода");
        }

        ActualExpense oldExpense = actualExpenseRepository.findById(expenseId).get();
        actualExpenseRepository.delete(oldExpense);
        expenseDto.setId(expenseId);
        ActualExpense newExpense = actualExpenseMapper.mapExpenseDtoToExpense(expenseDto);
        ActualExpense updatedExpense = actualExpenseRepository.save(newExpense);

        if(actualExpenseRepository.existsById(updatedExpense.getId())) {
            log.info("{} Расход успешно отредактирован", LocalDateTime.now());
            return actualExpenseMapper.mapExpenseToExpenseDto(updatedExpense);
        } else {
            throw new RuntimeException("Ошибка при редактировании расхода");
        }
    }

    @Override
    public void deleteExpense(Long userId, Long tripId, Long expenseId) {
        if (actualExpenseRepository.existsByTripId(expenseId)) {
            throw new ExpenseNotFoundException(expenseId);
        } else if (!actualExpenseRepository.existsByTripId(tripId)) {
            throw new ExpenseNotFoundForTripException(tripId);
        } else if (!actualExpenseRepository.findById(expenseId).get().getPaidByUserId().equals(userId)) {
            throw new RuntimeException("Не достаточно прав для удаления расхода");
        }

        actualExpenseRepository.deleteById(expenseId);
        log.info("{} Расход с id: {} успешно удален", LocalDateTime.now(), expenseId);
    }
}
