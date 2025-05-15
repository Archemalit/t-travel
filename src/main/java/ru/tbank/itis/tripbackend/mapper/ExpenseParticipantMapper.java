package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.ExpenseParticipantDto;
import ru.tbank.itis.tripbackend.model.ExpenseParticipant;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ExpenseParticipantMapper {
    ExpenseParticipantDto expenseParticipantToExpenseParticipantDto(ExpenseParticipant expenseParticipant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expense", ignore = true)
    @Mapping(target = "participant", ignore = true)
    ExpenseParticipant expenseParticipantDtoToExpenseParticipant(ExpenseParticipantDto expenseParticipantDto);
}
