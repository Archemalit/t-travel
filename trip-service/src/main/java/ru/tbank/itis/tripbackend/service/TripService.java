package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.TripRequest;
import ru.tbank.itis.tripbackend.dto.response.TripResponse;
import ru.tbank.itis.tripbackend.model.User;

import java.util.List;

public interface TripService {
    List<TripResponse> getAllTripsByUserId(Long userId, boolean onlyCreator, boolean onlyArchive);
    TripResponse getTripById(Long tripId, Long userId);
    TripResponse createTrip(TripRequest tripRequest, User user);
    TripResponse updateTrip(Long tripId, TripRequest tripRequest, Long userId);
    void deleteTrip(Long tripId, Long userId);
    void archiveTrip(Long tripId, Long userId);
}
