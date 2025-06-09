package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.PlannedExpenseDto;
import ru.tbank.itis.tripbackend.dto.request.PlannedExpenseRequest;
import ru.tbank.itis.tripbackend.dto.response.PlannedExpenseResponse;
import ru.tbank.itis.tripbackend.model.PlannedExpense;
import ru.tbank.itis.tripbackend.model.Trip;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PlannedExpenseMapper {
    @Mapping(target = "tripId", source = "trip.id")
    PlannedExpenseResponse mapExpenseToExpenseDto(PlannedExpense expense);
    @Mapping(target = "id", ignore = true)
    PlannedExpense mapExpenseDtoToExpense(PlannedExpenseRequest expenseDto, Trip trip);
}
