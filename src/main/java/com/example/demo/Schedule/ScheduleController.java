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

        @PostMapping("/actions/need/{actionId}")
        public ResponseEntity<?> chooseNeed (@PathVariable("actionId") Long actionId,@RequestBody ActionNeedRequest actionNeedRequest) {

            return ResponseEntity.ok().build();

        }


}



