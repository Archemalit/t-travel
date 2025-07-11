package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.request.ExpenseRequest;
import ru.tbank.itis.tripbackend.dto.response.ExpenseParticipantResponse;
import ru.tbank.itis.tripbackend.dto.response.ExpenseResponse;
import ru.tbank.itis.tripbackend.model.*;

import java.util.Set;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ExpenseMapper {
    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "paidByUserId", source = "paidBy.id")
    @Mapping(target = "participants", source = "participants")
    ExpenseResponse toDto(Expense expense);

    @Mapping(target = "expenseId", source = "expense.id")
    @Mapping(target = "participantId", source = "participant.user.id")
    @Mapping(target = "paidByUserId", source = "paidBy.id")
    ExpenseParticipantResponse toExpenseParticipantDto(ExpenseParticipant participant);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", source = "expenseResponse.description")
    @Mapping(target = "participants", ignore = true)
    Expense toEntity(ExpenseRequest expenseResponse, Trip trip, User paidBy);
}
