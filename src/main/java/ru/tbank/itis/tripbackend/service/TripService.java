package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.TripDto;

import java.util.List;

public interface TripService {
    List<TripDto> getAllTrips();
    TripDto getTripById(Long id);
    TripDto createTrip(TripDto tripDto);
    TripDto updateTrip(Long id, TripDto tripDto);
    void deleteTrip(Long id);
}
