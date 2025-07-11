package ru.tbank.itis.tripbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.itis.tripbackend.model.PlannedExpense;

import java.util.List;

public interface PlannedExpenseRepository extends JpaRepository<PlannedExpense, Long> {
    List<PlannedExpense> findAllByTripId(Long tripId);
    boolean existsByTripId(Long tripId);
}
