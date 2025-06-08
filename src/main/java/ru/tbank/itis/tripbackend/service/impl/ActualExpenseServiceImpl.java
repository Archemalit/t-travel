package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
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
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActualExpenseServiceImpl implements ActualExpenseService {
    private final ExpenseRepository expenseRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripRepository tripRepository;

    private final ExpenseMapper expenseMapper;

    @Override
    public ExpenseResponse getExpenseById(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        boolean participant = tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn
                (expense.getTrip().getId(), userId, List.of(TripParticipantStatus.ACCEPTED));
        if (!participant) {
            throw new ForbiddenAccessException("Вы не можете посмотреть этот расход, так как не являетесь участником этой поездки!");
        }

        return expenseMapper.toDto(expense);
    }

    @Override
    public List<ExpenseResponse> getAllExpensesByTrip(Long userId, Long tripId) {
        boolean participant = tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn
                (tripId, userId, List.of(TripParticipantStatus.ACCEPTED));
        if (!participant) {
            throw new ForbiddenAccessException("Вы не можете посмотреть этот расход, так как не являетесь участником этой поездки!");
        }

        return expenseRepository
                .findAllByTripId(tripId).stream()
                .map(expenseMapper::toDto).toList();
    }

//    @Override
//    public List<ExpenseParticipantResponse> getAllExpensesByTripAndMember(Long tripId, Long memberId) {
//
//        TripParticipant tripParticipant = tripParticipantRepository.findByTripIdAndUserId(tripId, memberId)
//                .orElseThrow(() -> new ParticipantNotFoundException(tripId, memberId));
//        return tripParticipant.getExpenseParticipations().stream().map(expenseParticipantMapper::toDto).toList();
//    }

    @Override
    @Transactional
    public ExpenseResponse createExpense(User paidBy, Long tripId, ExpenseRequest expenseDto) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        boolean isMember = tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn(trip.getId(), paidBy.getId(), List.of(TripParticipantStatus.ACCEPTED));
        if (!isMember) {
            throw new ForbiddenAccessException("Вы не являетесь участником поездки, поэтому не можете создать расход!");
        }
        Expense expense = expenseMapper.toEntity(expenseDto, trip, paidBy);

        Set<ExpenseParticipant> participants = new HashSet<>();
        Set<Long> paidForIds = new HashSet<>();
        for (ExpenseParticipantRequest expenseParticipant : expenseDto.getParticipants()) {
            TripParticipant participant = tripParticipantRepository.findByTripIdAndUserIdAndStatus
                            (tripId, expenseParticipant.getParticipantId(), TripParticipantStatus.ACCEPTED)
                            .orElseThrow(() -> new ParticipantNotFoundException(tripId, expenseParticipant.getParticipantId()));
            if (Objects.equals(participant.getUser().getId(), paidBy.getId())) {
//                так как расход на самого себя записал, его нигде отображать не нужно
                continue;
            }
            if (paidForIds.contains(participant.getId())) {
                throw new SeveralExpensesForUser(participant.getUser().getId());
            }
            participants.add(ExpenseParticipant.builder()
                            .expense(expense)
                            .participant(participant)
                            .paidBy(paidBy)
                            .amount(expenseParticipant.getAmount())
                    .build());
            paidForIds.add(participant.getId());
        }
        if (paidForIds.isEmpty()) { throw new ExpenseForMySelfException(); }
        expense.setParticipants(participants);
        expenseRepository.save(expense);
        return expenseMapper.toDto(expense);
    }

    @Override
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        if (!Objects.equals(expense.getPaidBy().getId(), userId)) {
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
