package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
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
}