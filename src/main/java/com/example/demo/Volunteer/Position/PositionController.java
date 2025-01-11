package com.example.demo.Volunteer.Position;

import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService PositionService;
    private final VolunteerService volunteerService;

    @Autowired
    public PositionController(PositionService PositionService, VolunteerService volunteerService) {
        this.PositionService = PositionService;
        this.volunteerService = volunteerService;
    }

    @GetMapping
    public Position[] getRoles() {
        return Position.values();
    }

    @GetMapping("/can-transition")
    public ResponseEntity<Boolean> canTransition(
            @RequestParam String fromRole,
            @RequestParam String toRole) {
        Position from = Position.valueOf(fromRole);
        Position to = Position.valueOf(toRole);
        Boolean canTransition = PositionService.canTransition(from, to);
        return ResponseEntity.ok(canTransition);
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignRole(
            @RequestParam ID userId,
            @RequestParam Position newPosition) {
        Volunteer volunteer = volunteerService.getVolunteerById(userId);

        PositionService.assignRole(volunteer, newPosition);
        return ResponseEntity.ok("Role assigned successfully: " + volunteer.getEmail() + " is now " + newPosition.toString());
    }

    @ExceptionHandler(PositionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleRoleException(PositionException e) {
        String message = e.getMessage();

        if (e.getFromPosition() != null && e.getFromPosition() == e.getToPosition()) {
            message = "The user is already in the role: " + e.getFromPosition().toString();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
