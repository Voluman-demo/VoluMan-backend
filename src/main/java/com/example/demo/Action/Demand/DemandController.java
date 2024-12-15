package com.example.demo.Action.Demand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demands")
@Tag(name = "Demand Management", description = "Endpoints for managing action demands")
public class DemandController {

    private final DemandRepository demandRepository;

    public DemandController(DemandRepository demandRepository) {
        this.demandRepository = demandRepository;
    }

    @GetMapping("")
    @Operation(summary = "Get all demands", description = "Retrieves a list of all demands related to actions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of demands"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Demand> getDemands() {
        return demandRepository.findAll();
    }
}
