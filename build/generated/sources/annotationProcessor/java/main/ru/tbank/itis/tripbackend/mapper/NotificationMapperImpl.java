package ru.tbank.itis.tripbackend.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dto.NotificationDto;
import ru.tbank.itis.tripbackend.model.Notification;
import ru.tbank.itis.tripbackend.model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-03T00:12:30+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDto toDto(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationDto.NotificationDtoBuilder notificationDto = NotificationDto.builder();

        notificationDto.userId( notificationUserId( notification ) );
        notificationDto.tripId( tripToTripId( notification.getTrip() ) );
        notificationDto.isRead( notification.isRead() );
        notificationDto.id( notification.getId() );
        notificationDto.type( notification.getType() );
        notificationDto.message( notification.getMessage() );

        return notificationDto.build();
    }

    @Override
    public Notification toEntity(NotificationDto notificationDto) {
        if ( notificationDto == null ) {
            return null;
        }

        Notification.NotificationBuilder notification = Notification.builder();

        notification.trip( tripIdToTrip( notificationDto.getTripId() ) );
        notification.message( notificationDto.getMessage() );
        if ( notificationDto.getIsRead() != null ) {
            notification.isRead( notificationDto.getIsRead() );
        }
        notification.type( notificationDto.getType() );

        return notification.build();
    }

    private Long notificationUserId(Notification notification) {
        if ( notification == null ) {
            return null;
        }
        User user = notification.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
