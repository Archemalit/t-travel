package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.request.TripRequest;
import ru.tbank.itis.tripbackend.dto.response.TripResponse;
import ru.tbank.itis.tripbackend.model.User;

import java.util.List;

public interface TripService {
    List<TripResponse> getAllTripsByUserId(Long id, boolean onlyCreator);
    TripResponse getTripById(Long id, User user);
    TripResponse createTrip(TripRequest tripRequest, User user);
    TripResponse updateTrip(Long id, TripRequest tripRequest, User user);
    void deleteTrip(Long id, User user);
}
