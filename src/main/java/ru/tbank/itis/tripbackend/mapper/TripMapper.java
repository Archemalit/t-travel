package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.TripDto;
import ru.tbank.itis.tripbackend.model.Trip;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TripMapper {
    TripDto tripToTripDto(Trip trip);
    @Mapping(target = "id", ignore = true)
    Trip tripDtoToTrip(TripDto tripDto);
}
