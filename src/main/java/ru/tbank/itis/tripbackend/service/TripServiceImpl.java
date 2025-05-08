package ru.tbank.itis.tripbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.itis.tripbackend.dto.TripDto;
import ru.tbank.itis.tripbackend.exception.TripNotFoundException;
import ru.tbank.itis.tripbackend.exception.ValidationException;
import ru.tbank.itis.tripbackend.mapper.TripMapper;
import ru.tbank.itis.tripbackend.model.Trip;
import ru.tbank.itis.tripbackend.repository.TripRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    private final TripMapper tripMapper;
    @Override
    public List<TripDto> getAllTrips() {
        return tripRepository.findAll()
                .stream()
                .map(tripMapper::tripToTripDto)
                .collect(Collectors.toList());
    }

    @Override
    public TripDto getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));
        return tripMapper.tripToTripDto(trip);
    }

    @Override
    public TripDto createTrip(TripDto tripDto) {
        Trip trip = tripMapper.tripDtoToTrip(tripDto);
        Trip savedTrip = tripRepository.save(trip);
        return tripMapper.tripToTripDto(savedTrip);
    }

    @Override
    public TripDto updateTrip(Long id, TripDto tripDto) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        if (tripDto.getTotalBudget() < 0) {
            throw new ValidationException("Бюджет не может быть отрицательным");
        }

        existingTrip.setTitle(tripDto.getTitle());
        existingTrip.setDescription(tripDto.getDescription());
        existingTrip.setStartDate(tripDto.getStartDate());
        existingTrip.setEndDate(tripDto.getEndDate());
        existingTrip.setTotalBudget(tripDto.getTotalBudget());

        Trip updatedTrip = tripRepository.save(existingTrip);
        return tripMapper.tripToTripDto(updatedTrip);
    }

    @Override
    public void deleteTrip(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new TripNotFoundException(id);
        }

        tripRepository.deleteById(id);
    }
}
