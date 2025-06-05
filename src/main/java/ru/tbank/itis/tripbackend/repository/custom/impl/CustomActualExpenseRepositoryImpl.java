package ru.tbank.itis.tripbackend.repository.custom.impl;

import jakarta.persistence.EntityManager;
import ru.tbank.itis.tripbackend.model.ActualExpense;
import ru.tbank.itis.tripbackend.repository.custom.CustomActualExpenseRepository;

public class CustomActualExpenseRepositoryImpl implements CustomActualExpenseRepository {

    private final EntityManager entityManager;

    public CustomActualExpenseRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean existsByTripId(Long tripId) {
        int expenseQuantity = entityManager.createQuery("select ae from ActualExpense ae where ae.tripId = ?",
                        ActualExpense.class)
                .setParameter(0, tripId)
                .getResultList()
                .size();
        return expenseQuantity >= 1;
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        int expenseQuantity = entityManager.createQuery("select ae from ActualExpense ae where ae.paidByUserId = ?",
                        ActualExpense.class)
                .setParameter(0, memberId)
                .getResultList()
                .size();
        return expenseQuantity >= 1;
    }
}
