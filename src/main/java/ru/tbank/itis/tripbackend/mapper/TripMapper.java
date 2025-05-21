package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import ru.tbank.itis.tripbackend.dto.request.TripRequest;
import ru.tbank.itis.tripbackend.dto.response.TripResponse;
import ru.tbank.itis.tripbackend.model.Trip;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TripMapper {
    TripResponse tripToTripDto(Trip trip);
    Trip tripDtoToTrip(TripRequest tripDto);
}
