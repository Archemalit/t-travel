package ru.tbank.itis.tripbackend.repository.custom;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomActualExpenseRepository {

    boolean existsByTripId(Long tripId);
    boolean existsByMemberId(Long memberId);
}
