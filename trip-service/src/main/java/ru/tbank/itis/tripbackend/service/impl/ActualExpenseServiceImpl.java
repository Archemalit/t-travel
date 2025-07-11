package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dto.DebtDto;
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

import java.math.BigDecimal;
import java.util.*;

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
        BigDecimal totalAmount = new BigDecimal(0);
        for (ExpenseParticipantRequest expenseParticipant : expenseDto.getParticipants()) {
            TripParticipant participant = tripParticipantRepository.findByTripIdAndUserIdAndStatus
                            (tripId, expenseParticipant.getParticipantId(), TripParticipantStatus.ACCEPTED)
                            .orElseThrow(() -> new ParticipantNotFoundException(tripId, expenseParticipant.getParticipantId()));
            if (paidForIds.contains(participant.getId())) {
                throw new SeveralExpensesForUser(participant.getUser().getId());
            }
            if (Objects.equals(participant.getUser().getId(), paidBy.getId())) {
//                так как расход на самого себя записал, его нигде отображать не нужно
                if (!paidForIds.contains(participant.getId())) {
                    totalAmount = totalAmount.add(expenseParticipant.getAmount());
                }
                paidForIds.add(participant.getId());
                continue;
            }
            totalAmount = totalAmount.add(expenseParticipant.getAmount());
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
        expense.setTotalAmount(totalAmount);

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

    @Override
    public List<DebtDto> getAllDebtsByTrip(Long tripId, Long userId) {
        boolean isParticipant = tripParticipantRepository.existsByTripIdAndUserIdAndStatusIn
                (tripId, userId, List.of(TripParticipantStatus.ACCEPTED));
        if (!isParticipant) {
            throw new ForbiddenAccessException("Вы не можете посмотреть список долгов, так как не являетесь участником этой поездки!");
        }

        List<Transaction> transactions = calculateDebts(expenseRepository.findAllByTripId(tripId));

        return transactions.stream()
                .map(transaction -> DebtDto.builder()
                        .tripId(tripId)
                        .amount(transaction.getAmount())
                        .debtorId(transaction.getDebtorId())
                        .creditorId(transaction.getCreditorId())
                        .build())
                .toList();
    }

    private List<Transaction> calculateDebts(List<Expense> expenses) {
        Map<Long, BigDecimal> balanceMap = new HashMap<>();

        for (Expense expense : expenses) {
            for (ExpenseParticipant p : expense.getParticipants()) {
                balanceMap.merge(p.getPaidBy().getId(), p.getAmount(), BigDecimal::add);
                balanceMap.merge(p.getParticipant().getUser().getId(), p.getAmount().negate(), BigDecimal::add);
            }
        }

        Queue<Map.Entry<Long, BigDecimal>> creditors = new LinkedList<>();
        Queue<Map.Entry<Long, BigDecimal>> debtors = new LinkedList<>();

        for (Map.Entry<Long, BigDecimal> entry : balanceMap.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.offer(entry);
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                entry.setValue(entry.getValue().negate());
                debtors.offer(entry);
            }
        }

        List<Transaction> result = new ArrayList<>();

        while (!debtors.isEmpty() && !creditors.isEmpty()) {
            var debtor = debtors.peek();
            var creditor = creditors.peek();
            BigDecimal min = debtor.getValue().min(creditor.getValue());

            result.add(Transaction.builder()
                    .debtorId(debtor.getKey())
                    .creditorId(creditor.getKey())
                    .amount(min)
                    .build());

            debtor.setValue(debtor.getValue().subtract(min));
            creditor.setValue(creditor.getValue().subtract(min));

            if (debtor.getValue().compareTo(BigDecimal.ZERO) == 0) debtors.poll();
            if (creditor.getValue().compareTo(BigDecimal.ZERO) == 0) creditors.poll();
        }

        return result;
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
