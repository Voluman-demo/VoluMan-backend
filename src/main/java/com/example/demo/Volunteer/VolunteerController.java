package com.example.demo.Volunteer;


import com.example.demo.Log.EventType;
import com.example.demo.Log.LogService;
import com.example.demo.Model.Errors;

import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.VolunteerDto.AdminRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {
    private final VolunteerRepository volunteerRepository;
    private final VolunteerService volunteerService;
    private final LogService logService;

    public VolunteerController(VolunteerRepository volunteerRepository, VolunteerService volunteerService, LogService logService) {
        this.volunteerRepository = volunteerRepository;
        this.volunteerService = volunteerService;
        this.logService = logService;
    }

    @GetMapping("")
    public ResponseEntity<List<Volunteer>> getVolunteers() {
        return ResponseEntity.ok(volunteerRepository.findAll());
    }

    @PostMapping("")
    public ResponseEntity<?> addVolunteer(@RequestBody VolunteerRequest request) {
        Long createdVolunteerId = volunteerService.createAndEditVolunteer(request);

        if (createdVolunteerId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdVolunteerId);
    }

    @DeleteMapping("/{volunteerId}/delete")
    public ResponseEntity<Void> deleteVolunteer(@PathVariable Long volunteerId, @RequestBody AdminRequest request) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Errors result = volunteerService.deleteVolunteer(volunteerId);
        if (result == Errors.SUCCESS) {
            logService.logVolunteer(null, EventType.DELETE, "Volunteer deleted by admin with id: " + request.adminId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{volunteerId}")
    public ResponseEntity<Volunteer> getVolunteer(@PathVariable Long volunteerId) {
        Volunteer volunteer = volunteerService.getVolunteerById(volunteerId);
        return volunteer != null ? ResponseEntity.ok(volunteer) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{volunteerId}/roles")
    public ResponseEntity<Void> changeRole(@PathVariable Long volunteerId, @RequestBody AdminRequest request, @RequestParam String role) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!volunteerRepository.existsById(volunteerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Errors result = volunteerService.assignPosition(volunteerId, Position.valueOf(role));
        if (result == Errors.SUCCESS) {
            logService.logVolunteer(null, EventType.UPDATE, "Promoted by admin with id: " + request.adminId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{volunteerId}/details")
    public ResponseEntity<Void> updateVolunteerDetails(@PathVariable Long volunteerId, @RequestBody VolunteerRequest details) {
        Errors result = volunteerService.editVolunteer(volunteerId, details);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{volunteerId}/availabilities")
    public ResponseEntity<Void> setAvailabilities(@PathVariable Long volunteerId, @RequestBody List<Availability> availabilities) {
        Errors result = volunteerService.setAvailabilities(volunteerId, availabilities);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{volunteerId}/availabilities")
    public ResponseEntity<List<Availability>> getAvailabilities(@PathVariable Long volunteerId) {
        List<Availability> availabilities = volunteerService.getAvailabilities(volunteerId);
        return availabilities != null ? ResponseEntity.ok(availabilities) : ResponseEntity.notFound().build();
    }

//    @PutMapping("/{volunteerId}/duties")
//    public ResponseEntity<Void> assignDuty(@PathVariable Long volunteerId, @RequestBody Duty duty) {
//        Errors result = volunteerService.assignDuty(volunteerId, duty);
//        if (result == Errors.SUCCESS) {
//            return ResponseEntity.ok().build();
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    @GetMapping("/{volunteerId}/duties")
//    public ResponseEntity<List<Duty>> getDuties(@PathVariable Long volunteerId) {
//        List<Duty> duties = volunteerService.getDuties(volunteerId);
//        return duties != null ? ResponseEntity.ok(duties) : ResponseEntity.notFound().build();
//    }

}
