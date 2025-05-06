package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.ExpenseCategoryDto;
import ru.tbank.itis.tripbackend.model.ExpenseCategory;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ExpenseCategoryMapper {
    ExpenseCategoryDto expenseCategoryToExpenseCategoryDto(ExpenseCategory expenseCategory);

    @Mapping(target = "id", ignore = true)
    ExpenseCategory expenseCategoryDtoToExpenseCategory(ExpenseCategoryDto expenseCategoryDto);
}
