package com.example.demo.Volunteer.Preferences;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/preferences")
@Tag(name = "Preferences Management", description = "Endpoints for managing volunteer preferences")
public class PreferencesController {

    private final PreferencesRepository preferencesRepository;

    public PreferencesController(PreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    @GetMapping
    @Operation(summary = "Get all preferences", description = "Retrieves a list of all volunteer preferences.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of preferences"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Preferences> getPreferences() {
        return preferencesRepository.findAll();
    }
}
