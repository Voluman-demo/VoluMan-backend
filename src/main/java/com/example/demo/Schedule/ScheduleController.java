package com.example.demo.Schedule;

import com.example.demo.Action.ActionDto.ActionScheduleDto;
import com.example.demo.Action.ActionRepository;
import com.example.demo.Log.EventType;
import com.example.demo.Log.LogService;
import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Schedule.ScheduleDto.*;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.VolunteerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/schedules")
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


//    @PostMapping("/actions/{actionId}/preferences")
//    public ResponseEntity<?> choosePref(
//            @PathVariable("actionId") Long actionId,
//            @RequestBody ActionPrefRequest actionPrefRequest
//    ) {
//        try {
//            if (!actionRepository.existsById(actionId)) {
//                return ResponseEntity.notFound().build();
//            }
//            if (!volunteerRepository.existsById(actionPrefRequest.volunteerId())) {
//                return ResponseEntity.notFound().build();
//            }
//            scheduleService.choosePref(actionId, actionPrefRequest);
//
//            logService.logSchedule(actionPrefRequest.volunteerId(), EventType.UPDATE, "Choose preferences");
//
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
        //TODO??

//    @PostMapping("/actions/{actionId}/demands")
//    public ResponseEntity<?> chooseDemands(
//            @PathVariable("actionId") ID actionId,
//            @RequestBody ActionNeedRequest actionNeedRequest
//    ) {
//        if (!actionRepository.existsById(actionId)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Action not found.");
//        }
//        if (!volunteerRepository.existsByIdAndPosition(actionNeedRequest.getLeaderId(), Position.LEADER)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Leader not authorized.");
//        }
//        if (!Objects.equals(actionRepository.findById(actionId).get().getLeaderId(), actionNeedRequest.getLeaderId())) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Leader does not match action.");
//        }
//
//        Errors result = scheduleService.scheduleNeedAction(actionId, actionNeedRequest);
//
//        if (result == Errors.SUCCESS) {
//            logService.logSchedule(actionNeedRequest.getLeaderId(), EventType.UPDATE, "Choose action needs with id: " + actionId);
//            return ResponseEntity.ok().body("Demands scheduled successfully.");
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to schedule demands.");
//    }



    @PostMapping("/generate")
    public ResponseEntity<?> generateSchedule(@RequestBody GenerateScheduleRequest generateScheduleRequest) {
        if (!volunteerRepository.existsByIdAndPosition(generateScheduleRequest.adminId(), Position.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin authorization required.");
        }

        Errors result = scheduleService.generateSchedule(generateScheduleRequest.date());

        if (result == Errors.SUCCESS) {
            logService.logSchedule(generateScheduleRequest.adminId(), EventType.CREATE, "Generate schedule");
            return ResponseEntity.ok().body("Schedule generated successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating schedule.");
    }

    @PutMapping("/{scheduleId}/modify")
    public ResponseEntity<?> modifySchedule(@PathVariable ID scheduleId,@RequestBody ModifyScheduleRequest modifyScheduleRequest) {
        if (!volunteerRepository.existsById(modifyScheduleRequest.volunteerId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Volunteer not found.");
        }

        Errors result = scheduleService.modifySchedule(scheduleId, modifyScheduleRequest);

        if (result == Errors.SUCCESS) {
            logService.logSchedule(modifyScheduleRequest.volunteerId(), EventType.UPDATE, "Schedule modified by volunteer with id: " + modifyScheduleRequest.volunteerId());
            return ResponseEntity.ok().body("Schedule modified successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error modifying schedule.");
    }

    @GetMapping("/actions/{actionId}")
    public ResponseEntity<?> getScheduleByAction(@PathVariable ID actionId) {
        if (!actionRepository.existsById(actionId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Action not found.");
        }

        List<Schedule> schedules = scheduleService.getActionSchedules(actionId);
        if (!schedules.isEmpty()) {
            return ResponseEntity.ok(schedules);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found for the action.");
    }

    @GetMapping("/volunteers/{volunteerId}")
    public ResponseEntity<?> getScheduleByVolunteer(@PathVariable ID volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Volunteer not found.");
        }

        List<Schedule> schedules= scheduleService.getVolunteerSchedules(volunteerId);
        if (!schedules.isEmpty()) {
            return ResponseEntity.ok(schedules);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found for the volunteer.");
    }


}



