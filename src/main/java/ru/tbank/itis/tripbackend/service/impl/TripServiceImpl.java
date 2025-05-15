package ru.tbank.itis.tripbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.itis.tripbackend.dto.TripDto;
import ru.tbank.itis.tripbackend.exception.TripNotFoundException;
import ru.tbank.itis.tripbackend.exception.UserNotFoundException;
import ru.tbank.itis.tripbackend.exception.ValidationException;
import ru.tbank.itis.tripbackend.mapper.TripMapper;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.TripRepository;
import ru.tbank.itis.tripbackend.repository.UserRepository;
import ru.tbank.itis.tripbackend.service.TripService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripMapper tripMapper;

    @Override
    @Transactional
    public List<TripDto> getAllTrips() {
        return tripRepository.findAll()
                .stream()
                .map(tripMapper::tripToTripDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TripDto getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));
        return tripMapper.tripToTripDto(trip);
    }

    @Override
    @Transactional
    public TripDto createTrip(TripDto tripDto) {
        validateTripDates(tripDto.getStartDate(), tripDto.getEndDate());

        Trip trip = tripMapper.tripDtoToTrip(tripDto);

        User creator = userRepository.findById(1L)
                .orElseThrow(() -> new UserNotFoundException(1L));
        trip.setCreator(creator);

        Trip savedTrip = tripRepository.save(trip);
        return tripMapper.tripToTripDto(savedTrip);
    }

    @Override
    @Transactional
    public TripDto updateTrip(Long id, TripDto tripDto) {
        validateTripDates(tripDto.getStartDate(), tripDto.getEndDate());

        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        existingTrip.setTitle(tripDto.getTitle());
        existingTrip.setDescription(tripDto.getDescription());
        existingTrip.setStartDate(tripDto.getStartDate());
        existingTrip.setEndDate(tripDto.getEndDate());
        existingTrip.setTotalBudget(tripDto.getTotalBudget());


        Trip updatedTrip = tripRepository.save(existingTrip);
        return tripMapper.tripToTripDto(updatedTrip);
    }

    @Override
    @Transactional
    public void deleteTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        tripRepository.delete(trip);
    }

    private void validateTripDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("Дата окончания поездки не может быть раньше даты начала");
        }
    }
}