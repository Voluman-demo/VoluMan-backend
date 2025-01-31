package com.example.demo.Action;

import com.example.demo.Action.ActionDto.ActionPrefRequest;
import com.example.demo.Action.ActionDto.ActionRequest;
import com.example.demo.Action.ActionDto.DescriptionRequest;
import com.example.demo.Model.Errors;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.VolunteerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/actions")
public class ActionController {

    private final ActionService actionService;
    private final VolunteerRepository volunteerRepository;

    public ActionController(ActionService actionService, VolunteerRepository volunteerRepository) {
        this.actionService = actionService;
        this.volunteerRepository = volunteerRepository;
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<Long>> getAllIds(@RequestParam Long volunteerId) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.ADMIN) && !volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.LEADER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(actionService.getAllIds());
    }

    @GetMapping("/{actionId}")
    public ResponseEntity<Action> getAction(@PathVariable Long actionId, @RequestParam Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId) || volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Action action = actionService.getAction(actionId);
        if (action != null) {
            return ResponseEntity.ok(action);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{actionId}/tmp")
    public ResponseEntity<Action> getActionTmp (@PathVariable Long actionId){
        return  ResponseEntity.ok(actionService.getActionTmp(actionId));
    }

    @GetMapping("/{actionId}/desc")
    public ResponseEntity<ArrayList<Description>> getActionDesc(@PathVariable Long actionId, @RequestParam Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId) || volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Action action = actionService.getAction(actionId);
        if (action != null) {
            return ResponseEntity.ok(new ArrayList<>(action.getDescr()));
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
    public ResponseEntity<Long> addAction(@RequestBody ActionRequest newAction, @RequestParam Long volunteerId) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long id = actionService.create();
        Errors result = actionService.updateAction(id, newAction);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.status(201).body(id);
        } else if (result == Errors.FAILURE) {
            return ResponseEntity.badRequest().body(id);
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
            @RequestBody DescriptionRequest newDesc
    ) {
        Errors result = actionService.setDesc(actionId, language, newDesc);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{actionId}/begin")
    public ResponseEntity<String> setBegin(@PathVariable Long actionId, @RequestBody Map<String, LocalDate> requestBody) {
        LocalDate begin = requestBody.get("begin");
        Errors result = actionService.setBeg(actionId, begin);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{actionId}/end")
    public ResponseEntity<String> setEnd(@PathVariable Long actionId, @RequestBody Map<String, LocalDate> requestBody
    ) {
        LocalDate end = requestBody.get("end");
        Errors result = actionService.setEnd(actionId, end);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{actionId}/preferences")
    public ResponseEntity<Errors> setPref(@PathVariable Long actionId, @RequestBody ActionPrefRequest actionPrefRequest) {

        Errors result = actionService.setPref(actionId, actionPrefRequest.getDecision(), actionPrefRequest.getVolunteerId());
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }




    //TODO dodać getDemands for an Action
}