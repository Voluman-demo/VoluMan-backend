package com.example.demo.Volunteer;


import com.example.demo.Action.Lang;
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

    @DeleteMapping("/{volunteerId}")
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


    @GetMapping("/{volunteerId}/candidates")
    public ResponseEntity<List<Volunteer>> getAllCandidates(@PathVariable Long volunteerId) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Volunteer> volunteers = volunteerService.getAllCandidates();
        return volunteers.isEmpty() ?  ResponseEntity.notFound().build() : ResponseEntity.ok(volunteers);
    }


    @PostMapping("/{volunteerId}/positions")
    public ResponseEntity<Position> getPosition(@PathVariable Long volunteerId, @RequestBody AdminRequest request) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!volunteerRepository.existsById(volunteerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Position result = volunteerService.getPosition(volunteerId);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{volunteerId}/positions")
    public ResponseEntity<Void> assignPosition(@PathVariable Long volunteerId, @RequestBody AdminRequest request, @RequestParam String position) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!volunteerRepository.existsById(volunteerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Errors result = volunteerService.assignPosition(volunteerId, Position.valueOf(position));
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

    @PutMapping("/{volunteerId}/limit-of-weekly-hours")
    public ResponseEntity<Void> updateVolunteerDetails(@PathVariable Long volunteerId, @RequestBody Double weeklyHours) {
        Errors result = volunteerService.editVolunteerWeeklyHours(volunteerId, weeklyHours);
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

    @GetMapping("/{volunteerId}/limit-of-weekly-hours")
    public ResponseEntity<Double> getLimitOfWeeklyHours(@PathVariable Long volunteerId) {
        Double limitOfWeeklyHours = volunteerService.getLimitOfWeeklyHours(volunteerId);
        return limitOfWeeklyHours != null ? ResponseEntity.ok(limitOfWeeklyHours) : ResponseEntity.notFound().build();
    }

    @PostMapping("")
    public ResponseEntity<List<PersonalData>> getPersData(@RequestParam Position position, @RequestBody AdminRequest request) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<PersonalData> personalDataList = volunteerService.getPersData(position);
        return personalDataList != null ? ResponseEntity.ok(personalDataList) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{volunteerId}/lang")
    public ResponseEntity<Lang> getLang(@PathVariable Long volunteerId, @RequestBody AdminRequest request) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.RECRUITER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Lang language = volunteerService.getLang(volunteerId);
            return language != null ? ResponseEntity.ok(language) : ResponseEntity.notFound().build();
    }


}
