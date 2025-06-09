package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.response.ExpenseParticipantResponse;
import ru.tbank.itis.tripbackend.model.ExpenseParticipant;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ExpenseParticipantMapper {
    @Mapping(target = "expenseId", source = "expense.id")
    @Mapping(target = "participantId", source = "participant.user.id")
    @Mapping(target = "paidByUserId", source = "paidBy.id")
    ExpenseParticipantResponse toDto(ExpenseParticipant expenseParticipant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expense", ignore = true)
    @Mapping(target = "participant", ignore = true)
    ExpenseParticipant toEntity(ExpenseParticipantResponse expenseParticipantResponse);
}
