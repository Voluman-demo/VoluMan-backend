package com.example.demo.Schedule;

import com.example.demo.Action.ActionDto.ActionScheduleDto;
import com.example.demo.Action.ActionRepository;
import com.example.demo.Log.EventType;
import com.example.demo.Log.LogService;
import com.example.demo.Schedule.ScheduleDto.*;
import com.example.demo.Volunteer.Role.VolunteerRole;
import com.example.demo.Volunteer.VolunteerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("")
@Tag(name = "Schedule Management", description = "Endpoints for managing schedules")
public class ScheduleController {
    private final LogService logService;
    private final ScheduleService scheduleService;
    private final ActionRepository actionRepository;
    private final VolunteerRepository volunteerRepository;

    public ScheduleController(ScheduleService scheduleService, ActionRepository actionRepository, VolunteerRepository volunteerRepository, LogService logService) {
        this.scheduleService = scheduleService;
        this.actionRepository = actionRepository;
        this.volunteerRepository = volunteerRepository;
        this.logService = logService;
    }

    @PostMapping("/actions/{actionId}/preferences")
    @Operation(summary = "Choose preferences for an action", description = "Allows a volunteer to choose preferences for a specific action.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preferences chosen successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Action or volunteer not found")
    })
    public ResponseEntity<?> choosePref(
            @Parameter(description = "ID of the action") @PathVariable("actionId") Long actionId,
            @RequestBody ActionPrefRequest actionPrefRequest
    ) {
        try {
            if (!actionRepository.existsById(actionId)) {
                return ResponseEntity.notFound().build();
            }
            if (!volunteerRepository.existsById(actionPrefRequest.volunteerId())) {
                return ResponseEntity.notFound().build();
            }
            scheduleService.choosePref(actionId, actionPrefRequest);

            logService.logSchedule(actionPrefRequest.volunteerId(), EventType.UPDATE, "Choose preferences");

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/actions/{actionId}/demands")
    @Operation(summary = "Choose demands for an action", description = "Allows a leader to choose demands for a specific action.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demands chosen successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not a leader"),
            @ApiResponse(responseCode = "404", description = "Action not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: Leader mismatch")
    })
    public ResponseEntity<?> chooseDemands(
            @Parameter(description = "ID of the action") @PathVariable("actionId") Long actionId,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int week,
            @RequestBody ActionNeedRequest actionNeedRequest
    ) {
        try {
            if (!actionRepository.existsById(actionId)) {
                return ResponseEntity.notFound().build();
            }
            if (!volunteerRepository.existsByVolunteerIdAndRole(actionNeedRequest.getLeaderId(), VolunteerRole.LEADER)) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (!Objects.equals(actionRepository.findById(actionId).get().getLeader().leaderId(), actionNeedRequest.getLeaderId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            scheduleService.scheduleNeedAction(actionId, year, week, actionNeedRequest);

            logService.logSchedule(actionNeedRequest.getLeaderId(), EventType.UPDATE, "Choose action needs with id: " + actionId);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/volunteers/{volunteerId}/availabilities")
    @Operation(summary = "Choose availabilities for a volunteer", description = "Allows volunteers to set their availabilities for a specific year and week.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Availabilities set successfully"),
            @ApiResponse(responseCode = "404", description = "Volunteer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<?> chooseAvail(
            @Parameter(description = "ID of the volunteer") @PathVariable Long volunteerId,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int week,
            @RequestBody VolunteerAvailRequest volunteerAvailRequest
    ) {
        try {
            if (!volunteerRepository.existsById(volunteerId)) {
                return ResponseEntity.notFound().build();
            }
            scheduleService.chooseAvailabilities(volunteerId, year, week, volunteerAvailRequest);

            logService.logSchedule(volunteerId, EventType.UPDATE, "Choose availabilities");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/schedules/generate")
    @Operation(summary = "Generate schedule", description = "Generates a schedule for a given year and week.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule generated successfully"),
            @ApiResponse(responseCode = "406", description = "Not acceptable: Date mismatch"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> generateSchedule(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int week,
            @RequestBody GenerateScheduleRequest generateScheduleRequest
    ) {
        try {
            // Walidacja: Sprawdzenie, czy użytkownik jest administratorem
            if (!volunteerRepository.existsByVolunteerIdAndRole(generateScheduleRequest.adminId(), VolunteerRole.ADMIN)) {
                return ResponseEntity.notFound().build();
            }

            // Obliczanie oczekiwanej daty na podstawie roku i tygodnia
            LocalDate expectedDate = LocalDate.ofYearDay(year, 1).plusWeeks(week - 1);
            LocalDate requestDate = generateScheduleRequest.date();

            // Sprawdzenie, czy data w żądaniu zgadza się z oczekiwaną datą
            if (!expectedDate.equals(requestDate)) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(expectedDate);
            }

            // Wywołanie usługi generowania harmonogramu
            scheduleService.generateSchedule(generateScheduleRequest.date());

            logService.logSchedule(generateScheduleRequest.adminId(), EventType.CREATE, "Generate schedule");
            return ResponseEntity.ok().body("Schedule generated successfully.");
        } catch (Exception e) {
            // Obsługa wyjątków i zwrócenie odpowiedzi z kodem błędu 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating schedule.");
        }
    }


    @PutMapping("/volunteers/{volunteerId}/schedules/modify")
    @Operation(
            summary = "Modify schedule for a volunteer",
            description = "Allows a volunteer to modify their schedule for a specific year and week."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule modified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or bad data"),
            @ApiResponse(responseCode = "404", description = "Volunteer not found")
    })
    public ResponseEntity<?> modifySchedule(
            @Parameter(description = "ID of the volunteer", required = true) @PathVariable Long volunteerId,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int week,
            @RequestBody ModifyScheduleRequest modifyScheduleRequest
    ) {
        try {
            if (!volunteerRepository.existsById(volunteerId)) {
                return ResponseEntity.notFound().build();
            }
            scheduleService.modifySchedule(volunteerId, year, week, modifyScheduleRequest);


            logService.logSchedule(volunteerId, EventType.UPDATE, "Schedule modified by volunteer with id: " + volunteerId);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/actions/{actionId}/schedules")
    @Operation(summary = "Get schedule for an action", description = "Retrieves the schedule associated with a specific action.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Action not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden for non-leaders"),
            @ApiResponse(responseCode = "409", description = "Leader mismatch")
    })
    public ResponseEntity<?> getScheduleByAction(
            @Parameter(description = "ID of the action") @PathVariable Long actionId,
            @RequestParam(defaultValue = "0") Long leaderId
    ) {
        //TODO w przyszłości rozbicie logiki na get od wolontariusza i od leadera
        try {
            if (!actionRepository.existsById(actionId)) {
                return ResponseEntity.notFound().build();
            }
            if (!volunteerRepository.existsByVolunteerIdAndRole(leaderId, VolunteerRole.LEADER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (!Objects.equals(actionRepository.findById(actionId).get().getLeader().leaderId(), leaderId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            ActionScheduleDto scheduleDto = scheduleService.getScheduleByAction(actionId);
            return ResponseEntity.ok(scheduleDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/volunteers/{volunteerId}/schedules")
    @Operation(
            summary = "Get schedule for a volunteer", description = "Retrieves the schedule for a specific volunteer based on the given year and week."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Volunteer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getScheduleByVolunteer(
            @Parameter(description = "ID of the volunteer", required = true) @PathVariable Long volunteerId,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int week
    ) {
        try {
            if (!volunteerRepository.existsById(volunteerId)) {
                return ResponseEntity.notFound().build();
            }

            VolunteerScheduleDto scheduleDto = scheduleService.getScheduleByVolunteer(volunteerId, year, week);
            return ResponseEntity.ok(scheduleDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}



