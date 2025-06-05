package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.ActualExpenseDto;
import ru.tbank.itis.tripbackend.dto.UserDto;
import ru.tbank.itis.tripbackend.mapper.impl.CustomActualExpenseMapper;
import ru.tbank.itis.tripbackend.model.ActualExpense;
import ru.tbank.itis.tripbackend.model.User;

import java.util.List;
import java.util.Set;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {UserMapper.class, CustomActualExpenseMapper.class})
public interface ActualExpenseMapper {
    ActualExpenseDto mapExpenseToExpenseDto(ActualExpense expense);

    ActualExpense mapExpenseDtoToExpense(ActualExpenseDto expenseDto, Set<User> members);
}
