package ru.tbank.itis.tripbackend.service;

import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.TripRequest;
import ru.tbank.itis.tripbackend.dto.response.TripResponse;
import ru.tbank.itis.tripbackend.model.User;

import java.util.List;

public interface TripService {
    List<TripResponse> getAllTripsByUserId(Long id, boolean onlyCreator, boolean onlyArchive);
    TripResponse getTripById(Long id, Long userId);
    TripResponse createTrip(TripRequest tripRequest, User user);
    TripResponse updateTrip(Long id, TripRequest tripRequest, Long userId);
    void deleteTrip(Long id, Long userId);
    void archiveTrip(Long tripId, Long userId);
}
