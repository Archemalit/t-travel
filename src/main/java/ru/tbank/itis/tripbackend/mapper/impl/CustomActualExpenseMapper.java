package ru.tbank.itis.tripbackend.mapper.impl;

import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dto.ActualExpenseDto;
import ru.tbank.itis.tripbackend.dto.UserDto;
import ru.tbank.itis.tripbackend.mapper.ActualExpenseMapper;
import ru.tbank.itis.tripbackend.model.ActualExpense;
import ru.tbank.itis.tripbackend.model.User;

import java.util.List;
import java.util.Set;

public class CustomActualExpenseMapper{
    public ActualExpense mapExpenseDtoToExpense(ActualExpenseDto expenseDto, Set<User> members) {
        return ActualExpense.builder()
                .id(expenseDto.getId())
                .tripId(expenseDto.getTripId())
                .amount(expenseDto.getAmount())
                .category(expenseDto.getCategory())
                .description(expenseDto.getDescription())
                .chequeImage(expenseDto.getChequeImage())
                .paidByUserId(expenseDto.getPaidByUserId())
                .members(members)
                .build();
    }
}
