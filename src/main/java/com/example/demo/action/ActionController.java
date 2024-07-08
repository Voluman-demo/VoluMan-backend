package com.example.demo.action;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/actions")
public class ActionController {
    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping("")
    public ResponseEntity<List<Action>> getActions() {
        return ResponseEntity.ok(actionService.getAllActions());
    }

    @GetMapping("/{idAction}")
    public ResponseEntity<Action> getAction(@PathVariable("idAction") Long idAction) {
        return actionService.getActionById(idAction)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{idAction}/description")
    public ResponseEntity<String> getActionDesc(@PathVariable("idAction") Long idAction) {
        return actionService.getActionDescription(idAction)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{idAction}/heading")
    public ResponseEntity<String> getActionHeading(@PathVariable("idAction") Long idAction) {
        return actionService.getActionHeading(idAction)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<?> addAction(@RequestBody Action action) {
        try {
            Action newAction = actionService.addAction(action);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{idAction}/close")
    public ResponseEntity<?> closeAction(@PathVariable("idAction") Long idAction, @RequestParam Long adminId) {
        try {
            actionService.closeAction(idAction, adminId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{idAction}/description")
    public ResponseEntity<?> changeDescription(@PathVariable("idAction") Long idAction, @RequestParam Long leaderId, @RequestParam String description) {
        try {
            actionService.changeDescription(idAction, leaderId, description);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
