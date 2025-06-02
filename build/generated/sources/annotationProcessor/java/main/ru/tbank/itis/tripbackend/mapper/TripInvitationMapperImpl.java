package ru.tbank.itis.tripbackend.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;
import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.TripInvitation;
import ru.tbank.itis.tripbackend.model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-03T00:12:30+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class TripInvitationMapperImpl implements TripInvitationMapper {

    @Override
    public TripInvitationDto toDto(TripInvitation tripInvitation) {
        if ( tripInvitation == null ) {
            return null;
        }

        TripInvitationDto.TripInvitationDtoBuilder tripInvitationDto = TripInvitationDto.builder();

        tripInvitationDto.tripId( tripInvitationTripId( tripInvitation ) );
        tripInvitationDto.invitedUserId( tripInvitationInvitedUserId( tripInvitation ) );
        tripInvitationDto.inviterId( tripInvitationInviterId( tripInvitation ) );
        tripInvitationDto.id( tripInvitation.getId() );
        tripInvitationDto.comment( tripInvitation.getComment() );
        if ( tripInvitation.getStatus() != null ) {
            tripInvitationDto.status( tripInvitation.getStatus().name() );
        }

        return tripInvitationDto.build();
    }

    @Override
    public TripInvitation toEntity(TripInvitationDto tripInvitationDto) {
        if ( tripInvitationDto == null ) {
            return null;
        }

        TripInvitation.TripInvitationBuilder tripInvitation = TripInvitation.builder();

        tripInvitation.comment( tripInvitationDto.getComment() );
        if ( tripInvitationDto.getStatus() != null ) {
            tripInvitation.status( Enum.valueOf( ForTripAndInvitationStatus.class, tripInvitationDto.getStatus() ) );
        }

        return tripInvitation.build();
    }

    private Long tripInvitationTripId(TripInvitation tripInvitation) {
        if ( tripInvitation == null ) {
            return null;
        }
        Trip trip = tripInvitation.getTrip();
        if ( trip == null ) {
            return null;
        }
        Long id = trip.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long tripInvitationInvitedUserId(TripInvitation tripInvitation) {
        if ( tripInvitation == null ) {
            return null;
        }
        User invitedUser = tripInvitation.getInvitedUser();
        if ( invitedUser == null ) {
            return null;
        }
        Long id = invitedUser.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long tripInvitationInviterId(TripInvitation tripInvitation) {
        if ( tripInvitation == null ) {
            return null;
        }
        User inviter = tripInvitation.getInviter();
        if ( inviter == null ) {
            return null;
        }
        Long id = inviter.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
