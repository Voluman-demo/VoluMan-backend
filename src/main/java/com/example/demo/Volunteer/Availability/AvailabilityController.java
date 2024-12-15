package com.example.demo.Volunteer.Availability;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/availabilities")
@Tag(name = "Availability Management", description = "Endpoints for managing volunteer availabilities")
public class AvailabilityController {

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityController(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @GetMapping
    @Operation(summary = "Get all availabilities", description = "Retrieves a list of all volunteer availabilities.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of availabilities")
    })
    public List<Availability> getAllAvailability() {
        return availabilityRepository.findAll();
    }
}
