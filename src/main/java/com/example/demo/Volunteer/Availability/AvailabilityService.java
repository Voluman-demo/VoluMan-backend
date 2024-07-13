package com.example.demo.Volunteer.Availability;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    public void addAvail(Availability availability) {
        availabilityRepository.save(availability);
    }

    public Availability getByVolunteerIdAndDate(Long volunteerId, LocalDate requestDate) {
        return availabilityRepository.findByVolunteer_VolunteerIdAndDate(volunteerId, requestDate)
                .orElse(new Availability());
    }

    public List<Availability> getAvailabilitiesForDay(LocalDate date) {
        return availabilityRepository.findAllByDate(date);
    }
}
