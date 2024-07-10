package com.example.demo.Volunteer;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/volunteer")
public class VolunteerController {
    private final VolunteerRepository volunteerRepository;
    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerRepository volunteerRepository, VolunteerService volunteerService) {
        this.volunteerRepository = volunteerRepository;
        this.volunteerService = volunteerService;
    }

    @GetMapping("/{idVolunteer}")
    public ResponseEntity<Volunteer> getVolunteer(@PathVariable Long idVolunteer) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);
        if (volunteer.isPresent()) {
            return ResponseEntity.ok(volunteer.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("")
    public List<Volunteer> getVolunteers() {
        return volunteerRepository.findAll();
    }

    @GetMapping("/{idVolunteer}/leader")
    public ResponseEntity<Volunteer> getVolunteerLeader(@PathVariable Long idVolunteer) {
        Optional<Volunteer> leader = volunteerRepository.findByVolunteerIdAndRole(idVolunteer, VolunteerRole.LEADER);
        if (leader.isPresent()) {
            return ResponseEntity.ok(leader.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/leader")
    public List<Volunteer> getVolunteerLeaders() {
        List<Volunteer> leaders = volunteerRepository.findAllByRole(VolunteerRole.LEADER);
        if (leaders.isEmpty()) {
            return (List<Volunteer>) ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(leaders).getBody();
    }


    @PutMapping("/{idVolunteer}/promote")
    public ResponseEntity<Volunteer> promoteToLeader(@PathVariable Long idVolunteer, @RequestParam Long adminId) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(adminId, VolunteerRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Zwrot odpowiedzi 403 Forbidden, gdy użytkownik nie jest adminem
        }
        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);

        if (volunteer.isPresent()) {
            volunteerService.promoteToLeader(idVolunteer);
            return ResponseEntity.ok(volunteer.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{idVolunteer}/degrade")
    public ResponseEntity<Volunteer> degradeLeader(@PathVariable Long idVolunteer, @RequestParam Long adminId) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(adminId, VolunteerRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Zwrot odpowiedzi 403 Forbidden, gdy użytkownik nie jest adminem
        }
        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);

        if (volunteer.isPresent()) {
            volunteerService.degradeLeader(idVolunteer);
            return ResponseEntity.ok(volunteer.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{idVolunteer}/delete")
    public ResponseEntity<Void> deleteVolunteer(@PathVariable Long idVolunteer, @RequestParam Long adminId) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(adminId, VolunteerRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Zwrot odpowiedzi 403 Forbidden, gdy użytkownik nie jest adminem
        }
        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);
        if (volunteer.isPresent()) {
            volunteerRepository.deleteById(idVolunteer);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }


}
