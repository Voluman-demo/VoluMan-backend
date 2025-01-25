package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.User.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping("/actions")
public class ActionController {

    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<ID>> getActions() {
        return ResponseEntity.ok(actionService.getAllIds());
    }

    @GetMapping("/{actionId}")
    public ResponseEntity<Action> getAction(@PathVariable ID actionId) {
        Action action = actionService.getAction(actionId);
        if (action != null) {
            return ResponseEntity.ok(action);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{actionId}/desc")
    public ResponseEntity<ArrayList<Version>> getActionDesc(@PathVariable ID actionId) {
        Action action = actionService.getAction(actionId);
        if (action != null) {
            return ResponseEntity.ok(new ArrayList<>(action.getDescr().values()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{actionId}/heading")
    public ResponseEntity<String> getActionHeading(@PathVariable ID actionId, @RequestParam Lang language) {
        Description description = actionService.getDesc(actionId, language);
        if (description != null) {
            return ResponseEntity.ok(description.getFullName());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("")
    public ResponseEntity<ID> addAction(@RequestBody Action newAction) {
        ID id = actionService.create();
        Errors result = actionService.updateAction(id, newAction);
        if(result == Errors.SUCCESS){
            return ResponseEntity.status(201).body(id);
        }
        return ResponseEntity.internalServerError().body(id);
    }

    @DeleteMapping("/{actionId}")
    public ResponseEntity<Void> deleteAction(@PathVariable ID actionId) {
        Errors result = actionService.remove(actionId);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{actionId}/desc")
    public ResponseEntity<String> changeDesc(
            @PathVariable ID actionId,
            @RequestParam Lang language,
            @RequestBody Description newDesc
    ) {
        Errors result = actionService.setDesc(actionId, language, newDesc);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok("Description updated successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{actionId}/begin")
    public ResponseEntity<String> setBegin(@PathVariable ID actionId, @RequestParam LocalDate begin) {
        Errors result = actionService.setBeg(actionId, begin);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok("Begin date set successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{actionId}/end")
    public ResponseEntity<String> setEnd(@PathVariable ID actionId, @RequestParam LocalDate end) {
        Errors result = actionService.setEnd(actionId, end);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok("End date set successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{actionId}/preferences/stronglymine")
    public ResponseEntity<String> addStronglyMine(@PathVariable ID actionId, @RequestBody User user) {
        Errors result = actionService.setStronglyMine(user, actionId);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok("Added to Strongly Mine successfully.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add to Strongly Mine.");
    }

    @PostMapping("/{actionId}/preferences/weaklymine")
    public ResponseEntity<String> addWeaklyMine(@PathVariable ID actionId, @RequestBody User user) {
        Errors result = actionService.setWeaklyMine(user, actionId);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok("Added to Weakly Mine successfully.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add to Weakly Mine.");
    }

    @PostMapping("/{actionId}/preferences/rejected")
    public ResponseEntity<String> addRejected(@PathVariable ID actionId, @RequestBody User user) {
        Errors result = actionService.setRejected(user, actionId);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok("Added to Rejected successfully.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add to Rejected.");
    }

    @PostMapping("/{actionId}/preferences/undecided")
    public ResponseEntity<String> addUndecided(@PathVariable ID actionId, @RequestBody User user) {
        Errors result = actionService.setUndecided(user, actionId);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok("Added to Undecided successfully.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add to Undecided.");
    }

    @GetMapping("/preferences/stronglymine")
    public ResponseEntity<ArrayList<Description>> getStronglyMine(@RequestBody User user) {
        return ResponseEntity.ok(actionService.getStronglyMine(user));
    }

    @GetMapping("/preferences/weaklymine")
    public ResponseEntity<ArrayList<Description>> getWeaklyMine(@RequestBody User user) {
        return ResponseEntity.ok(actionService.getWeaklyMine(user));
    }

    @GetMapping("/preferences/rejected")
    public ResponseEntity<ArrayList<Description>> getRejected(@RequestBody User user) {
        return ResponseEntity.ok(actionService.getRejected(user));
    }

    @GetMapping("/preferences/undecided")
    public ResponseEntity<ArrayList<Description>> getUndecided(@RequestBody User user) {
        return ResponseEntity.ok(actionService.getUndecided(user));
    }

}