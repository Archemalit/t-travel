package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.ActualExpenseDto;
import ru.tbank.itis.tripbackend.model.ActualExpense;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ActualExpenseMapper {
    @Mapping(target = "id", ignore = true)
    ActualExpenseDto mapExpenseToExpenseDto(ActualExpense expense);
    @Mapping(target = "id", ignore = true)
    ActualExpense mapExpenseDtoToExpense(ActualExpenseDto expenseDto);
}
