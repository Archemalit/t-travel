package ru.tbank.itis.tripbackend.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dto.request.TripRequest;
import ru.tbank.itis.tripbackend.dto.response.TripResponse;
import ru.tbank.itis.tripbackend.model.Trip;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-29T17:41:51+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class TripMapperImpl implements TripMapper {

    @Override
    public TripResponse toDto(Trip trip) {
        if ( trip == null ) {
            return null;
        }

        TripResponse.TripResponseBuilder tripResponse = TripResponse.builder();

        tripResponse.id( trip.getId() );
        tripResponse.status( trip.getStatus() );
        tripResponse.title( trip.getTitle() );
        tripResponse.description( trip.getDescription() );
        tripResponse.startDate( trip.getStartDate() );
        tripResponse.endDate( trip.getEndDate() );
        tripResponse.totalBudget( trip.getTotalBudget() );

        return tripResponse.build();
    }

    @Override
    public Trip toEntity(TripRequest tripDto) {
        if ( tripDto == null ) {
            return null;
        }

        Trip.TripBuilder trip = Trip.builder();

        trip.title( tripDto.getTitle() );
        trip.description( tripDto.getDescription() );
        trip.startDate( tripDto.getStartDate() );
        trip.endDate( tripDto.getEndDate() );
        trip.totalBudget( tripDto.getTotalBudget() );

        return trip.build();
    }
}
