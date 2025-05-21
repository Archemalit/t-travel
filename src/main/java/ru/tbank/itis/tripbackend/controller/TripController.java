package ru.tbank.itis.tripbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.request.TripRequest;
import ru.tbank.itis.tripbackend.dto.response.TripResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.TripService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Log4j2
public class TripController {

    private final TripService tripService;

    @GetMapping
    public List<TripResponse> getAllTrips(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestParam(name = "onlyCreator", required = false, defaultValue = "false") boolean onlyCreator) {
        return tripService.getAllTripsByUserId(userDetails.getId(), onlyCreator);
    }

    @GetMapping("/{id}")
    public TripResponse getTripById(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @PathVariable("id") Long id) {
        return tripService.getTripById(id, userDetails.getUser());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse createTrip(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @Valid @RequestBody TripRequest tripRequest) {
        return tripService.createTrip(tripRequest, userDetails.getUser());
    }

    @PutMapping("/{id}")
    public TripResponse updateTrip(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @PathVariable Long id, @Valid @RequestBody TripRequest tripRequest) {
        return tripService.updateTrip(id, tripRequest, userDetails.getUser());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @PathVariable Long id) {
        tripService.deleteTrip(id, userDetails.getUser());
    }
}