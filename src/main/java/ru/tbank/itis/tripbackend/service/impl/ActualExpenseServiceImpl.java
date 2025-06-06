package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.itis.tripbackend.dto.request.ExpenseParticipantRequest;
import ru.tbank.itis.tripbackend.dto.request.ExpenseRequest;
import ru.tbank.itis.tripbackend.dto.response.ExpenseParticipantResponse;
import ru.tbank.itis.tripbackend.dto.response.ExpenseResponse;
import ru.tbank.itis.tripbackend.exception.*;
import ru.tbank.itis.tripbackend.mapper.ExpenseMapper;
import ru.tbank.itis.tripbackend.mapper.ExpenseParticipantMapper;
import ru.tbank.itis.tripbackend.mapper.TripParticipantMapper;
import ru.tbank.itis.tripbackend.model.*;
import ru.tbank.itis.tripbackend.repository.ExpenseRepository;
import ru.tbank.itis.tripbackend.repository.TripParticipantRepository;
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.service.ActualExpenseService;
import ru.tbank.itis.tripbackend.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActualExpenseServiceImpl implements ActualExpenseService {
    private final ExpenseRepository expenseRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    private final ExpenseMapper expenseMapper;
    private final ExpenseParticipantMapper expenseParticipantMapper;

    @Override
    public ExpenseResponse getExpenseById(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        return expenseMapper.toDto(expense);
    }

    @Override
    public List<ExpenseResponse> getAllExpensesByTrip(Long tripId) {
        return expenseRepository
                .findAllByTripId(tripId).stream()
                .map(expenseMapper::toDto).toList();
    }

    @Override
    public List<ExpenseParticipantResponse> getAllExpensesByTripAndMember(Long tripId, Long memberId) {
        TripParticipant tripParticipant = tripParticipantRepository.findByTripIdAndUserId(tripId, memberId)
                .orElseThrow(() -> new ParticipantNotFoundException(tripId, memberId));
        return tripParticipant.getExpenseParticipations().stream().map(expenseParticipantMapper::toDto).toList();
    }

    @Override
    public ExpenseResponse createExpense(ExpenseRequest expenseDto) {
        Trip trip = tripRepository.findById(expenseDto.getTripId())
                .orElseThrow(() -> new TripNotFoundException(expenseDto.getTripId()));
        User paidBy = userRepository.findById(expenseDto.getPaidByUserId())
                .orElseThrow(() -> new UserNotFoundException(expenseDto.getPaidByUserId()));

        Expense expense = expenseMapper.toEntity(expenseDto, trip, paidBy);

        Set<ExpenseParticipant> participants = new HashSet<>();
        for (ExpenseParticipantRequest expenseParticipant : expenseDto.getParticipants()) {
            TripParticipant participant = tripParticipantRepository.findByTripIdAndUserId(expenseDto.getTripId(), expenseParticipant.getParticipantId())
                            .orElseThrow(() -> new ParticipantNotFoundException(expenseDto.getTripId(), expenseParticipant.getParticipantId()));
            participants.add(ExpenseParticipant.builder()
                            .expense(expense)
                            .participant(participant)
                            .paidBy(paidBy)
                            .amount(expenseParticipant.getAmount())
                    .build());
        }
        expense.setParticipants(participants);
        expenseRepository.save(expense);

        return expenseMapper.toDto(expense);
    }

    @Override
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        if (expense.getPaidBy().getId().equals(userId)) {
            throw new ForbiddenAccessException("Только создатель траты может её удалить!");
        }

        expenseRepository.deleteById(expenseId);
    }

//    @Override
//    public ActualExpenseDto updateExpense(Long userId, Long tripId, Long expenseId, ActualExpenseDto expenseDto) {
//        Optional<ActualExpense> expense = actualExpenseRepository.findById(expenseId);
//        if (expense.isEmpty()) {
//            throw new ExpenseNotFoundException(expenseId);
//        } else if (!actualExpenseRepository.existsByTripId(tripId)) {
//            throw new ExpenseNotFoundForTripException(tripId);
//        } else if (!expense.get().getPaidByUserId().equals(userId)) {
//            throw new RuntimeException("Не достаточно прав для редактирования расхода");
//        }
//
//        ActualExpense oldExpense = actualExpenseRepository.findById(expenseId).get();
//        actualExpenseRepository.delete(oldExpense);
//        expenseDto.setId(expenseId);
//        Set<User> members = userService.getUserSetByUserDtoSet(expenseDto.getMembers());
//        ActualExpense newExpense = actualExpenseMapper.toEntity(expenseDto, members);
//        ActualExpense updatedExpense = actualExpenseRepository.save(newExpense);
//
//        if (actualExpenseRepository.existsById(updatedExpense.getId())) {
//            log.info("{} Расход успешно отредактирован", LocalDateTime.now());
//            return actualExpenseMapper.toDto(updatedExpense);
//        } else {
//            throw new RuntimeException("Ошибка при редактировании расхода");
//        }
//    }
//
}
