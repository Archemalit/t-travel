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
    @Mapping(source = "trip.id", target = "tripId")
    @Mapping(source = "read", target = "isRead")
    NotificationDto toDto(Notification notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trip", ignore = true)
    Notification toEntity(NotificationDto notificationDto);

}