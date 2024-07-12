package com.example.demo.Schedule;

import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.action.ActionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleRepository scheduleRepository;
    private final ActionRepository actionRepository;
    private final VolunteerRepository volunteerRepository;

    public ScheduleController(ScheduleService scheduleService, ScheduleRepository scheduleRepository, ActionRepository actionRepository,VolunteerRepository volunteerRepository) {
        this.scheduleService = scheduleService;
        this.scheduleRepository = scheduleRepository;
        this.actionRepository = actionRepository;
        this.volunteerRepository = volunteerRepository;
    }


//    @GetMapping("/{year}/{week}/actions/{actionId}")
//    public ResponseEntity<Schedule> getSchedule(@PathVariable("year") int year, @PathVariable("week") int week, @PathVariable("actionId") int actionId) {
//        //validacja
//
//        return ResponseEntity.ok(scheduleService.showSchedule(year, week, actionId));
//    }

    @PostMapping("/actions/{actionId}")
    public ResponseEntity<?> choosePref(@PathVariable("actionId") Long actionId, @RequestBody ActionDecisionRequest actionDecisionRequest) {
        try {
            if (!actionRepository.existsById(actionId)) {
                return ResponseEntity.notFound().build();
            }
            if (!volunteerRepository.existsById(actionDecisionRequest.volunteerId)) {
                return ResponseEntity.notFound().build();
            }
            scheduleService.choosePref(actionId, actionDecisionRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{year}/{week}/actions/{actionId}")
    public ResponseEntity<?> chooseNeed(@PathVariable("year") int year, @PathVariable("week") int week, @PathVariable("actionId") Long actionId, @RequestBody ActionNeedRequest actionNeedRequest) {
        try {
            scheduleService.scheduleWeeklyAction(actionId, year, week, actionNeedRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{year}/{week}/volunteers/{volunteerId}")
    public ResponseEntity<?> chooseAvail(@PathVariable("year") int year, @PathVariable("week") int week, @PathVariable("volunteerId") Long volunteerId, @RequestBody VolunteerAvailRequest volunteerAvailRequest) {
        try {
            scheduleService.chooseAvailabilities(volunteerId, year, week, volunteerAvailRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{year}/{week}/schedule")
    public ResponseEntity<?> generateSchedule(@PathVariable("year") int year, @PathVariable("week") int week, @RequestBody GenerateScheduleRequest generateScheduleRequest) {
        //walidacja

        scheduleService.generateSchedule(generateScheduleRequest.getDay());
    }

}



