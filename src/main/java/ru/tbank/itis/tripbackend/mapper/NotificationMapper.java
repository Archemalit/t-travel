package ru.tbank.itis.tripbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tbank.itis.tripbackend.dto.NotificationDto;
import ru.tbank.itis.tripbackend.model.Notification;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface NotificationMapper {
    NotificationDto notificationToNotificationDto(Notification notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "user", ignore = true)
    Notification notificationDtoToNotification(NotificationDto notificationDto);
}
