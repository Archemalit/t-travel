package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.TripParticipantDto;
import ru.tbank.itis.tripbackend.model.TripParticipant;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TripParticipantMapper {
    TripParticipantDto toDto(TripParticipant tripParticipant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "expenseParticipations", ignore = true)
    TripParticipant toEntity(TripParticipantDto tripParticipantDto);
}
