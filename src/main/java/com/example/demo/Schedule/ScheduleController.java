package com.example.demo.Schedule;

import com.example.demo.Action.ActionRepository;
import com.example.demo.Action.Demand.UpdateNeedDto;
import com.example.demo.Log.EventType;
import com.example.demo.Log.LogService;
import com.example.demo.Model.Errors;
import com.example.demo.Schedule.ScheduleDto.GenerateScheduleRequest;
import com.example.demo.Schedule.ScheduleDto.ModifyScheduleRequest;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.VolunteerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {
    private final LogService logService;
    private final ScheduleService scheduleService;
    private final ActionRepository actionRepository;
    private final VolunteerRepository volunteerRepository;
    private final ScheduleRepository scheduleRepository;

    public ScheduleController(ScheduleService scheduleService, ActionRepository actionRepository, VolunteerRepository volunteerRepository, LogService logService, ScheduleRepository scheduleRepository) {
        this.scheduleService = scheduleService;
        this.actionRepository = actionRepository;
        this.volunteerRepository = volunteerRepository;
        this.logService = logService;
        this.scheduleRepository = scheduleRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateSchedule(@RequestBody GenerateScheduleRequest generateScheduleRequest) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(generateScheduleRequest.adminId(), Position.ADMIN)) {
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
    public ResponseEntity<?> modifySchedule(@PathVariable Long scheduleId, @RequestBody ModifyScheduleRequest modifyScheduleRequest) {
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

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long scheduleId) {
        Errors result = scheduleService.deleteSchedule(scheduleId);

        if (result == Errors.SUCCESS) {
            logService.logSchedule(scheduleId, EventType.DELETE, "Schedule deleted with Long: " + scheduleId);
            return ResponseEntity.ok("Schedule deleted successfully.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found.");
    }

    @GetMapping("/{scheduleId}")
    public Optional<Schedule> getScheduleById(@PathVariable Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

    @GetMapping("/actions/{actionId}")
    public ResponseEntity<?> getScheduleByAction(@PathVariable Long actionId, @RequestParam Long volunteerId) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.LEADER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!actionRepository.existsById(actionId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Schedule> schedules = scheduleService.getActionSchedules(actionId);
        if (!schedules.isEmpty()) {
            return ResponseEntity.ok(schedules);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/volunteers/{volunteerId}")
    public ResponseEntity<?> getScheduleByVolunteer(@PathVariable Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Schedule> schedules = scheduleService.getVolunteerSchedules(volunteerId);
        if (!schedules.isEmpty()) {
            return ResponseEntity.ok(schedules);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/actions/{actionId}/demands")
    public ResponseEntity<String> updateNeed(
            @PathVariable Long actionId,
            @RequestBody UpdateNeedDto updateNeedDto
    ) {
        if (!volunteerRepository.existsByVolunteerIdAndPosition(updateNeedDto.getVolunteerId(), Position.LEADER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Errors result = scheduleService.updateDemand(actionId, updateNeedDto);
        if (result == Errors.SUCCESS) {
            return ResponseEntity.ok().build();
        } else if (result == Errors.CREATED) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
