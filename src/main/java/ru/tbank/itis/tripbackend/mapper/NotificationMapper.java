package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.tbank.itis.tripbackend.dto.NotificationDto;
import ru.tbank.itis.tripbackend.model.Notification;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface NotificationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "trip", target = "tripId", qualifiedByName = "tripToTripId")
    @Mapping(source = "read", target = "isRead")
    NotificationDto toDto(Notification notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trip", source = "tripId", qualifiedByName = "tripIdToTrip")
    Notification toEntity(NotificationDto notificationDto);

    @Named("tripToTripId")
    default Long tripToTripId(String trip) {
        return trip != null ? Long.parseLong(trip) : null;
    }

    @Named("tripIdToTrip")
    default String tripIdToTrip(Long tripId) {
        return tripId != null ? tripId.toString() : null;
    }
}