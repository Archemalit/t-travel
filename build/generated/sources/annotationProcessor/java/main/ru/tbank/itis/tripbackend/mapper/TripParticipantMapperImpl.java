package ru.tbank.itis.tripbackend.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dictionary.TripParticipantStatus;
import ru.tbank.itis.tripbackend.dto.TripParticipantDto;
import ru.tbank.itis.tripbackend.model.TripParticipant;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-23T23:40:17+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class TripParticipantMapperImpl implements TripParticipantMapper {

    @Override
    public TripParticipantDto toDto(TripParticipant tripParticipant) {
        if ( tripParticipant == null ) {
            return null;
        }

        TripParticipantDto.TripParticipantDtoBuilder tripParticipantDto = TripParticipantDto.builder();

        tripParticipantDto.id( tripParticipant.getId() );
        if ( tripParticipant.getStatus() != null ) {
            tripParticipantDto.status( tripParticipant.getStatus().name() );
        }

        return tripParticipantDto.build();
    }

    @Override
    public TripParticipant toEntity(TripParticipantDto tripParticipantDto) {
        if ( tripParticipantDto == null ) {
            return null;
        }

        TripParticipant.TripParticipantBuilder tripParticipant = TripParticipant.builder();

        if ( tripParticipantDto.getStatus() != null ) {
            tripParticipant.status( Enum.valueOf( TripParticipantStatus.class, tripParticipantDto.getStatus() ) );
        }

        return tripParticipant.build();
    }
}
