package ru.tbank.itis.tripbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.itis.tripbackend.model.ActualExpense;
import ru.tbank.itis.tripbackend.repository.custom.CustomActualExpenseRepository;

import java.util.List;

public interface ActualExpenseRepository extends JpaRepository<ActualExpense, Long>, CustomActualExpenseRepository {
    List<ActualExpense> findAllByTripId(Long tripId);
}
