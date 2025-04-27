package ru.tbank.itis.tripbackend.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dto.TripDto;
import ru.tbank.itis.tripbackend.model.Trip;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-27T20:24:53+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class TripMapperImpl implements TripMapper {

    @Override
    public TripDto tripToTripDto(Trip trip) {
        if ( trip == null ) {
            return null;
        }

        TripDto.TripDtoBuilder tripDto = TripDto.builder();

        tripDto.id( trip.getId() );
        tripDto.title( trip.getTitle() );
        tripDto.description( trip.getDescription() );
        tripDto.startDate( trip.getStartDate() );
        tripDto.endDate( trip.getEndDate() );
        tripDto.totalBudget( trip.getTotalBudget() );

        return tripDto.build();
    }

    @Override
    public Trip tripDtoToTrip(TripDto tripDto) {
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
