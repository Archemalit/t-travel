package ru.tbank.itis.tripbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.TripDto;
import ru.tbank.itis.tripbackend.service.TripService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Log4j2
public class TripController {

    private final TripService tripService;

    @GetMapping
    public List<TripDto> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/{id}")
    public TripDto getTripById(@PathVariable("id") long id) {
        return tripService.getTripById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripDto createTrip(@Valid @RequestBody TripDto tripDto) {
        return tripService.createTrip(tripDto);
    }

    @PutMapping("/{id}")
    public TripDto updateTrip(@PathVariable Long id, @Valid @RequestBody TripDto tripDto) {
        return tripService.updateTrip(id, tripDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
    }
}