package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.PlannedExpenseDto;
import ru.tbank.itis.tripbackend.model.PlannedExpense;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PlannedExpenseMapper {
    @Mapping(target = "id", ignore = true)
    PlannedExpenseDto mapExpenseToExpenseDto(PlannedExpense expense);
    @Mapping(target = "id", ignore = true)
    PlannedExpense mapExpenseDtoToExpense(PlannedExpenseDto expenseDto);
}
