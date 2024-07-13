package com.example.demo.Volunteer.Availability;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("availabilities")
public class AvailabilityController {
    private final AvailabilityRepository availabilityRepository;
    public AvailabilityController(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }
    @GetMapping
    public List<Availability> getAllAvailability() {
        return availabilityRepository.findAll();
    }
}
