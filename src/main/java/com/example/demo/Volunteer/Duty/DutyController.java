package com.example.demo.Volunteer.Duty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/duties")
@Tag(name = "Duty Management", description = "Endpoints for managing volunteer duties")
public class DutyController {

    private final DutyRepository dutyRepository;

    public DutyController(DutyRepository dutyRepository) {
        this.dutyRepository = dutyRepository;
    }

    @GetMapping
    @Operation(summary = "Get all duties", description = "Retrieves a list of all volunteer duties.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of duties")
    })
    public List<Duty> getAllDuty() {
        return dutyRepository.findAll();
    }
}
