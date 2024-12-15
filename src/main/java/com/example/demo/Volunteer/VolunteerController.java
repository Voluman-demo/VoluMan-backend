package com.example.demo.Volunteer;

import com.example.demo.Log.EventType;
import com.example.demo.Log.LogService;
import com.example.demo.Volunteer.Role.RoleService;
import com.example.demo.Volunteer.Role.VolunteerRole;
import com.example.demo.Volunteer.VolunteerDto.AdminRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/volunteers")
@Tag(name = "Volunteer Management", description = "Endpoints for managing volunteers")
public class VolunteerController {

    private final VolunteerRepository volunteerRepository;
    private final VolunteerService volunteerService;
    private final LogService logService;
    private final RoleService roleService;

    public VolunteerController(VolunteerRepository volunteerRepository, VolunteerService volunteerService, LogService logService, RoleService roleService) {
        this.volunteerRepository = volunteerRepository;
        this.volunteerService = volunteerService;
        this.logService = logService;
        this.roleService = roleService;
    }

    @GetMapping("")
    @Operation(summary = "Get all volunteers", description = "Retrieves a list of all volunteers.")
    @ApiResponse(responseCode = "200", description = "List of volunteers retrieved successfully")
    public ResponseEntity<List<Volunteer>> getVolunteers() {
        return ResponseEntity.ok(volunteerRepository.findAll());
    }

    @PostMapping("")
    @Operation(summary = "Add a new volunteer", description = "Adds a new volunteer to the system.")
    @ApiResponse(responseCode = "200", description = "Volunteer added successfully")
    public ResponseEntity<Volunteer> addVolunteer(
            @Parameter(description = "Volunteer details to add", required = true) @RequestBody Volunteer volunteer) {
        return ResponseEntity.ok(volunteerService.addVolunteer(volunteer));
    }

    @DeleteMapping("/{volunteerId}/delete")
    @Operation(summary = "Delete a volunteer by ID", description = "Deletes a volunteer based on their ID.")
    @ApiResponse(responseCode = "200", description = "Volunteer deleted successfully")
    public ResponseEntity<Void> deleteVolunteer(
            @Parameter(description = "ID of the volunteer to delete", example = "1") @PathVariable Long volunteerId) {
        volunteerRepository.deleteById(volunteerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{idVolunteer}")
    @Operation(summary = "Get volunteer by ID", description = "Retrieves a volunteer by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volunteer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Volunteer not found")
    })
    public ResponseEntity<Volunteer> getVolunteer(
            @Parameter(description = "ID of the volunteer", example = "1") @PathVariable Long idVolunteer) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);
        return volunteer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/leaders")
    @Operation(summary = "Get all volunteer leaders", description = "Retrieves a list of all volunteers with the LEADER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of leaders retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No leaders found")
    })
    public ResponseEntity<List<Volunteer>> getVolunteerLeaders() {
        List<Volunteer> leaders = volunteerRepository.findAllByRole(VolunteerRole.LEADER);
        if (leaders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(leaders);
    }

    @GetMapping("/leaders/{idVolunteer}")
    @Operation(summary = "Get leader by ID", description = "Retrieves a volunteer with the LEADER role by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leader retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Leader not found")
    })
    public ResponseEntity<Volunteer> getVolunteerLeader(
            @Parameter(description = "ID of the volunteer leader", example = "1") @PathVariable Long idVolunteer) {
        Optional<Volunteer> leader = volunteerRepository.findByVolunteerIdAndRole(idVolunteer, VolunteerRole.LEADER);
        return leader.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{idVolunteer}/roles")
    @Operation(summary = "Change role of a volunteer", description = "Changes the role of a volunteer, requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role changed successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not an admin"),
            @ApiResponse(responseCode = "404", description = "Volunteer not found")
    })
    public ResponseEntity<Void> changeRole(
            @Parameter(description = "ID of the volunteer", example = "1") @PathVariable Long idVolunteer,
            @Parameter(description = "Admin request containing admin ID") @RequestBody AdminRequest request,
            @Parameter(description = "New role for the volunteer", example = "LEADER") @RequestParam String role) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!volunteerRepository.existsByVolunteerId(idVolunteer)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            roleService.assignRole(vol, VolunteerRole.valueOf(role));
            logService.logVolunteer(vol, EventType.UPDATE, "Promoted by admin with id: " + request.adminId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{idVolunteer}")
    @Operation(summary = "Delete a volunteer", description = "Deletes a volunteer, requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volunteer deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not an admin"),
            @ApiResponse(responseCode = "404", description = "Volunteer not found")
    })
    public ResponseEntity<Void> deleteVolunteer(
            @Parameter(description = "ID of the volunteer", example = "1") @PathVariable Long idVolunteer,
            @Parameter(description = "Admin request containing admin ID") @RequestBody AdminRequest request) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);
        if (volunteer.isPresent()) {
            volunteerRepository.deleteById(idVolunteer);
            logService.logVolunteer(volunteer.get(), EventType.DELETE, "Volunteer deleted by admin with id: " + request.adminId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
