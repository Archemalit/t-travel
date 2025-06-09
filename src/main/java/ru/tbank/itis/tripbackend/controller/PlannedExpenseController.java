package ru.tbank.itis.tripbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.PlannedExpenseDto;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.PlannedExpenseService;

import java.util.List;

@RestController
@RequestMapping("/{tripId}/plannedExpenses")
@RequiredArgsConstructor
public class PlannedExpenseController {

    private final PlannedExpenseService plannedExpenseService;

    @GetMapping("/{expenseId}")
    public PlannedExpenseDto getExpenseById(@PathVariable Long tripId, @PathVariable Long expenseId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return plannedExpenseService.getExpenseById(tripId, userDetails.getId(), expenseId);
    }

    @GetMapping("/list")
    public List<PlannedExpenseDto> getAllExpensesByTripId(@PathVariable Long tripId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return plannedExpenseService.getAllExpensesByTripId(tripId, userDetails.getId());
    }

    @PostMapping()
    public PlannedExpenseDto createExpense(@PathVariable Long tripId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @Valid @RequestBody PlannedExpenseDto expenseDto) {
        return plannedExpenseService.createExpense(tripId, userDetails.getId(), expenseDto);
    }

    @PutMapping("/{expenseId}")
    public PlannedExpenseDto updateExpense(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @PathVariable Long expenseId, @Valid @RequestBody PlannedExpenseDto expenseDto) {
        return plannedExpenseService.updateExpense(tripId, userDetails.getId(), expenseId, expenseDto);
    }

    @DeleteMapping("/{expenseId}")
    public void deleteExpense(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails,
                              @PathVariable Long expenseId) {
        plannedExpenseService.deleteExpense(tripId, userDetails.getId(), expenseId);
    }
}
