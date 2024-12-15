package com.example.demo.Volunteer.Role;

import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "Endpoints for managing roles")
public class RoleController {

    private final RoleService roleService;
    private final VolunteerService volunteerService;

    @Autowired
    public RoleController(RoleService roleService, VolunteerService volunteerService) {
        this.roleService = roleService;
        this.volunteerService = volunteerService;
    }

    @GetMapping
    @Operation(summary = "Get all available roles", description = "Returns a list of all available volunteer roles.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of roles")
    public VolunteerRole[] getRoles() {
        return VolunteerRole.values();
    }

    @GetMapping("/can-transition")
    @Operation(summary = "Check role transition", description = "Check if a transition between two roles is allowed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully checked role transition"),
            @ApiResponse(responseCode = "400", description = "Invalid role provided", content = @Content)
    })
    public ResponseEntity<Boolean> canTransition(
            @Parameter(description = "Current role of the volunteer", example = "CANDIDATE")
            @RequestParam String fromRole,
            @Parameter(description = "Role to transition to", example = "VOLUNTEER")
            @RequestParam String toRole) {
        VolunteerRole from = VolunteerRole.valueOf(fromRole);
        VolunteerRole to = VolunteerRole.valueOf(toRole);
        Boolean canTransition = roleService.canTransition(from, to);
        return ResponseEntity.ok(canTransition);
    }

    @PostMapping("/assign")
    @Operation(summary = "Assign a new role to a volunteer", description = "Assigns a specified role to a volunteer by their user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid role assignment or user not found", content = @Content)
    })
    public ResponseEntity<String> assignRole(
            @Parameter(description = "ID of the user to whom the role is assigned", example = "1")
            @RequestParam Long userId,
            @Parameter(description = "New role to be assigned", example = "VOLUNTEER")
            @RequestParam VolunteerRole newVolunteerRole) {
        Volunteer volunteer = volunteerService.findVolunteerById(userId);

        roleService.assignRole(volunteer, newVolunteerRole);
        return ResponseEntity.ok("Role assigned successfully: " + volunteer.getVolunteerDetails().getEmail() + " is now " + newVolunteerRole.toString());
    }

    @ExceptionHandler(RoleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Operation(summary = "Handle role exceptions", description = "Handles exceptions related to invalid role assignments.")
    @ApiResponse(responseCode = "400", description = "Role assignment error", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<String> handleRoleException(RoleException e) {
        String message = e.getMessage();

        if (e.getFromVolunteerRole() != null && e.getFromVolunteerRole() == e.getToVolunteerRole()) {
            message = "The user is already in the role: " + e.getFromVolunteerRole().toString();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
