package ru.tbank.itis.tripbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.ActualExpenseDto;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.ActualExpenseService;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@RestController
@RequestMapping("/{tripId}/expenses")
@RequiredArgsConstructor
@Log4j2
public class ActualExpenseController {

    private final ActualExpenseService actualExpenseService;

    @GetMapping
    public List<ActualExpenseDto> getAllExpensesByTrip(@PathVariable Long tripId) {
        return actualExpenseService.getAllExpensesByTrip(tripId);
    }

    @GetMapping("/{memberId}")
    public List<ActualExpenseDto> getAllExpensesByTripMember(@PathVariable Long tripId, @PathVariable Long memberId) {
        return actualExpenseService.getAllExpensesByTripMember(tripId, memberId);
    }

    @PostMapping
    public ActualExpenseDto createExpense(@Valid @RequestBody ActualExpenseDto expenseDto) {
        return actualExpenseService.createExpense(expenseDto);
    }

    @PutMapping("/{expenseId}")
    public ActualExpenseDto updateExpense(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long tripId,
                                          @PathVariable Long expenseId, @Valid @RequestBody ActualExpenseDto expenseDto) {

        Long userId = userDetails.getUser().getId();

        return actualExpenseService.updateExpense(userId, tripId, expenseId, expenseDto);
    }

    @DeleteMapping("/{expenseId}")
    public void deleteExpense(@AuthenticationPrincipal UserDetailsImpl userDetails,
                              @PathVariable Long tripId, @PathVariable Long expenseId) {

        Long userId = userDetails.getUser().getId();

        actualExpenseService.deleteExpense(userId, tripId, expenseId);
    }
}
