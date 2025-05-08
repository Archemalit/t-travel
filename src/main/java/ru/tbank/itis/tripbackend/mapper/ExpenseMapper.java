package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.ExpenseDto;
import ru.tbank.itis.tripbackend.model.Expense;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ExpenseMapper {
    ExpenseDto expenseToExpenseDto(Expense expense);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "paidBy", ignore = true)
    @Mapping(target = "participants", ignore = true)
    Expense expenseDtoToExpense(ExpenseDto expenseDto);
}
