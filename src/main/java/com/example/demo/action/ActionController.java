package com.example.demo.action;

import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;


@RestController
@RequestMapping("/actions")
public class ActionController {
    private final ActionService actionService;
    private final ActionRepository actionRepository;
    private final VolunteerRepository volunteerRepository;

    public ActionController(ActionService actionService, ActionRepository actionRepository, VolunteerRepository volunteerRepository) {
        this.actionService = actionService;
        this.actionRepository = actionRepository;
        this.volunteerRepository = volunteerRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<Action>> getActions() { //DONE
        return ResponseEntity.ok(actionService.getAllActions());
    }

    @GetMapping("/{idAction}")
    public ResponseEntity<Action> getAction(@PathVariable("idAction") Long idAction) { //DONE
        return actionService.getActionById(idAction)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{idAction}/description")
    public ResponseEntity<String> getActionDesc(@PathVariable("idAction") Long idAction) { //DONE
        return actionService.getActionDescription(idAction)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{idAction}/heading")
    public ResponseEntity<String> getActionHeading(@PathVariable("idAction") Long idAction) { //DONE
        return actionService.getActionHeading(idAction)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<?> addAction(@RequestBody AddActionRequest request) {
        if(actionService.getLeader(request.leaderId()).isEmpty()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Action newAction = actionService.createAndAddAction(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{idAction}/description")
    public ResponseEntity<?> changeDescription(@PathVariable("idAction") Long idAction, @RequestBody ChangeDescriptionRequest request) {
        if(!volunteerRepository.existsByVolunteerIdAndRole(request.leaderId(), VolunteerRole.LEADER)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(!actionRepository.existsById(idAction)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            actionService.changeDescription(idAction, request.leaderId(), request.description());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{idAction}/close")
    public ResponseEntity<?> closeAction(@PathVariable("idAction") Long idAction, @RequestBody CloseActionRequest request) { //DONE
        if(!volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(!actionRepository.existsById(idAction)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            actionService.closeAction(idAction, request.adminId());
            return ResponseEntity.ok().build();
        }
         catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
