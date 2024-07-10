package com.example.demo.Volunteer;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {
    private final VolunteerRepository volunteerRepository;
    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerRepository volunteerRepository, VolunteerService volunteerService) {
        this.volunteerRepository = volunteerRepository;
        this.volunteerService = volunteerService;
    }

    @GetMapping("")
    public ResponseEntity<List<Volunteer>> getVolunteers() { //DONE
        return ResponseEntity.ok(volunteerRepository.findAll());
    }

    @GetMapping("/{idVolunteer}")
    public ResponseEntity<Volunteer> getVolunteer(@PathVariable Long idVolunteer) { //DONE
        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);
        return volunteer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/leaders")
    public ResponseEntity<List<Volunteer>> getVolunteerLeaders() { //DONE
        List<Volunteer> leaders = volunteerRepository.findAllByRole(VolunteerRole.LEADER);
        if (leaders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(leaders);
    }

    @GetMapping("/leaders/{idVolunteer}")
    public ResponseEntity<Volunteer> getVolunteerLeader(@PathVariable Long idVolunteer) { //DONE
        Optional<Volunteer> leader = volunteerRepository.findByVolunteerIdAndRole(idVolunteer, VolunteerRole.LEADER);
        return leader.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{idVolunteer}/promote")
    public ResponseEntity<Void> promoteToLeader(@PathVariable Long idVolunteer, @RequestBody AdminRequest request) { //DONE
        if (!volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);
        if (volunteer.isPresent()) {
            volunteerService.promoteToLeader(idVolunteer);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{idVolunteer}/degrade")
    public ResponseEntity<Void> degradeLeader(@PathVariable Long idVolunteer, @RequestBody AdminRequest request) { //DONE
        if (!volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);
        if (volunteer.isPresent()) {
            volunteerService.degradeLeader(idVolunteer);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{idVolunteer}")
    public ResponseEntity<Void> deleteVolunteer(@PathVariable Long idVolunteer, @RequestBody AdminRequest request) { //DONE
        if (!volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)) {
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
