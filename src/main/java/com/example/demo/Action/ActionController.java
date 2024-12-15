package com.example.demo.Action;

import com.example.demo.Action.ActionDto.*;
import com.example.demo.Log.EventType;
import com.example.demo.Log.LogService;
import com.example.demo.Volunteer.Role.VolunteerRole;
import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actions")
@Tag(name = "Action Management", description = "Endpoints for managing actions")
public class ActionController {

    private final ActionService actionService;
    private final ActionRepository actionRepository;
    private final VolunteerRepository volunteerRepository;
    private final VolunteerService volunteerService;
    private final LogService logService;

    public ActionController(ActionService actionService, ActionRepository actionRepository, VolunteerRepository volunteerRepository, VolunteerService volunteerService, LogService logService) {
        this.actionService = actionService;
        this.actionRepository = actionRepository;
        this.volunteerRepository = volunteerRepository;
        this.volunteerService = volunteerService;
        this.logService = logService;
    }

    @GetMapping("")
    @Operation(summary = "Get all actions", description = "Retrieves a list of all actions.")
    @ApiResponse(responseCode = "200", description = "List of actions retrieved successfully")
    public ResponseEntity<List<Action>> getActions() {
        return ResponseEntity.ok(actionService.getAllActions());
    }

    @GetMapping("/{idAction}")
    @Operation(summary = "Get action by ID", description = "Retrieves an action by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Action not found")
    })
    public ResponseEntity<Action> getAction(
            @Parameter(description = "ID of the action to be retrieved", example = "1") @PathVariable("idAction") Long idAction) {
        return actionService.getActionById(idAction)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{idAction}/description")
    @Operation(summary = "Get action description", description = "Retrieves the description of an action by its ID.")
    public ResponseEntity<DescriptionResponse> getActionDesc(
            @Parameter(description = "ID of the action", example = "1") @PathVariable("idAction") Long idAction) {
        return actionService.getActionDescription(idAction).map(DescriptionResponse::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    @Operation(summary = "Add a new action", description = "Creates and adds a new action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Action created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<?> addAction(
            @Parameter(description = "Request body containing action details", required = true) @RequestBody AddActionRequest request) {
        if (!volunteerService.isLeaderExist(request.leaderId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Action newAction = actionService.createAndAddAction(request);
            logService.logAction(request.adminId(), EventType.ADD, "Admin added action with id:" + newAction.getActionId());
            return ResponseEntity.status(HttpStatus.CREATED).body(newAction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{actionId}")
    @Operation(summary = "Delete an action", description = "Deletes an action by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Action not found")
    })
    public ResponseEntity<?> deleteAction(
            @Parameter(description = "ID of the action to be deleted", example = "1") @PathVariable Long actionId) {
        actionRepository.deleteById(actionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{idAction}/description")
    @Operation(summary = "Change action description", description = "Updates the description of an existing action.")
    public ResponseEntity<?> changeDescription(
            @Parameter(description = "ID of the action", example = "1") @PathVariable("idAction") Long idAction,
            @Parameter(description = "Request body containing new description", required = true) @RequestBody ChangeDescriptionRequest request) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(request.leaderId(), VolunteerRole.LEADER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!actionRepository.existsById(idAction)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            actionService.changeDescription(idAction, request.description());
            logService.logAction(request.leaderId(), EventType.UPDATE, "Leader updated description of action with id:" + idAction);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{idAction}/close")
    @Operation(summary = "Close an action", description = "Closes an action by its ID.")
    public ResponseEntity<?> closeAction(
            @Parameter(description = "ID of the action", example = "1") @PathVariable("idAction") Long idAction,
            @Parameter(description = "Request body containing admin ID", required = true) @RequestBody CloseActionRequest request) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!actionRepository.existsById(idAction)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            actionService.closeAction(idAction, request.adminId());
            logService.logAction(request.adminId(), EventType.UPDATE, "Admin closed action with id:" + idAction);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
