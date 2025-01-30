package com.example.demo.Action;

import com.example.demo.Model.Errors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/actions")
public class ActionController {

    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<Long>> getActions() {
        return ResponseEntity.ok(actionService.getAllIds());
    }

    @GetMapping("/{actionId}")
    public ResponseEntity<Action> getAction(@PathVariable Long actionId) {
        Action action = actionService.getAction(actionId);
        if (action != null) {
            return ResponseEntity.ok(action);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{actionId}/desc")
    public ResponseEntity<ArrayList<Version>> getActionDesc(@PathVariable Long actionId) {
        Action action = actionService.getAction(actionId);
        if (action != null) {
            return ResponseEntity.ok(new ArrayList<>(action.getDescr().values()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{actionId}/heading")
    public ResponseEntity<String> getActionHeading(@PathVariable Long actionId, @RequestParam Lang language) {
        Description description = actionService.getDesc(actionId, language);
        if (description != null) {
            return ResponseEntity.ok(description.getFullName());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("//heading")
    public ResponseEntity<List<String>> getAllHeadings(@RequestParam Lang language) {
        List<String> headings = actionService.getAllHeadings(language);
        if (!headings.isEmpty()) {
            return ResponseEntity.ok(headings);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("")
    public ResponseEntity<Long> addAction(@RequestBody Action newAction) {
        Long id = actionService.create();
        Errors result = actionService.updateAction(id, newAction);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.status(201).body(id);
        }
        return ResponseEntity.internalServerError().body(id);
    }

    @DeleteMapping("/{actionId}")
    public ResponseEntity<Void> deleteAction(@PathVariable Long actionId) {
        Errors result = actionService.remove(actionId);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{actionId}/desc")
    public ResponseEntity<String> changeDesc(
            @PathVariable Long actionId,
            @RequestParam Lang language,
            @RequestBody Description newDesc
    ) {
        Errors result = actionService.setDesc(actionId, language, newDesc);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{actionId}/begin")
    public ResponseEntity<String> setBegin(@PathVariable Long actionId, @RequestParam LocalDate begin) {
        Errors result = actionService.setBeg(actionId, begin);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{actionId}/end")
    public ResponseEntity<String> setEnd(@PathVariable Long actionId, @RequestParam LocalDate end) {
        Errors result = actionService.setEnd(actionId, end);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{actionId}/preferences/{preference}/volunteers/{volunteerId}")
    public ResponseEntity<Errors> setPref(@PathVariable Long actionId, @PathVariable String preference, @PathVariable Long volunteerId) {

        Errors result = actionService.setPref(actionId, preference, volunteerId);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

//    @PostMapping("/{actionId}/preferences/weakly-mine")
//    public ResponseEntity<String> addWeaklyMine(@PathVariable Long actionId, @RequestBody User user) {
//        Errors result = actionService.setWeaklyMine(user, actionId);
//        if (result == Errors.SUCCESS) {
//            return ResponseEntity.ok("Added to Weakly Mine successfully.");
//        }
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//    }
//
//    @PostMapping("/{actionId}/preferences/rejected")
//    public ResponseEntity<String> addRejected(@PathVariable Long actionId, @RequestBody User user) {
//        Errors result = actionService.setRejected(user, actionId);
//        if (result == Errors.SUCCESS) {
//            return ResponseEntity.ok().build();
//        }
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//    }
//
//    @PostMapping("/{actionId}/preferences/undecided")
//    public ResponseEntity<String> addUndecided(@PathVariable Long actionId, @RequestBody User user) {
//        Errors result = actionService.setUndecided(user, actionId);
//        if (result == Errors.SUCCESS) {
//            return ResponseEntity.ok().build();
//        }
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//    }

    @GetMapping("/preferences/{preference}/volunteers/{volunteerId}")
    public ResponseEntity<List<Description>> getPref(@PathVariable String preference, @PathVariable Long volunteerId) {
        List<Description> prefList = actionService.getPref(preference, volunteerId);

        return prefList.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(prefList);
    }

//    @GetMapping("/preferences/weakly-mine")
//    public ResponseEntity<ArrayList<Description>> getWeaklyMine(@RequestBody User user) {
//        return ResponseEntity.ok(actionService.getWeaklyMine(user));
//    }
//
//    @GetMapping("/preferences/rejected")
//    public ResponseEntity<ArrayList<Description>> getRejected(@RequestBody User user) {
//        return ResponseEntity.ok(actionService.getRejected(user));
//    }
//
//    @GetMapping("/preferences/undecided")
//    public ResponseEntity<ArrayList<Description>> getUndecided(@RequestBody User user) {
//        return ResponseEntity.ok(actionService.getUndecided(user));
//    }


}