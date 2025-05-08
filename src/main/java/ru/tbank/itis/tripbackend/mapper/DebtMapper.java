package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.DebtDto;
import ru.tbank.itis.tripbackend.model.Debt;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DebtMapper {
    DebtDto debtToDebtDto(Debt debt);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "debtor", ignore = true)
    @Mapping(target = "creditor", ignore = true)
    Debt debtDtoToDebt(DebtDto debtDto);
}
