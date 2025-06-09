package ru.tbank.itis.tripbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.PlannedExpenseDto;
import ru.tbank.itis.tripbackend.service.PlannedExpenseService;

import java.util.List;

@RestController
@RequestMapping("/{tripId}/plannedExpenses")
@RequiredArgsConstructor
public class PlannedExpenseController {

    private final PlannedExpenseService plannedExpenseService;

    @GetMapping()
    public List<PlannedExpenseDto> getAllExpensesByTripId(@PathVariable Long tripId) {
        return plannedExpenseService.getAllExpensesByTripId(tripId);
    }

    @PostMapping()
    public PlannedExpenseDto createExpense(@PathVariable Long tripId,
                                           @Valid @RequestBody PlannedExpenseDto expenseDto) {
        return plannedExpenseService.createExpense(tripId, expenseDto);
    }

    @PutMapping("/{expenseId}")
    public PlannedExpenseDto updateExpense(@PathVariable Long tripId, @PathVariable Long expenseId,
                                           @Valid @RequestBody PlannedExpenseDto expenseDto) {
        return plannedExpenseService.updateExpense(tripId, expenseId, expenseDto);
    }

    @DeleteMapping("/{expenseId}")
    public void deleteExpense(@PathVariable Long tripId, @PathVariable Long expenseId) {
        plannedExpenseService.deleteExpense(tripId, expenseId);
    }
}
