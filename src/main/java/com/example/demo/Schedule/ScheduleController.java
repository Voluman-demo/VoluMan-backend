package com.example.demo.Schedule;

import com.example.demo.Schedule.Dto.*;
import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerRole;
import com.example.demo.action.ActionRepository;
import com.example.demo.action.Dto.ActionScheduleDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ActionRepository actionRepository;
    private final VolunteerRepository volunteerRepository;

    public ScheduleController(ScheduleService scheduleService, ActionRepository actionRepository,VolunteerRepository volunteerRepository) {
        this.scheduleService = scheduleService;
        this.actionRepository = actionRepository;
        this.volunteerRepository = volunteerRepository;
    }


    @PostMapping("/actions/{actionId}/preference")
    public ResponseEntity<?> choosePref(@PathVariable("actionId") Long actionId, @RequestBody ActionPrefRequest actionPrefRequest) {
        try {
            if (!actionRepository.existsById(actionId)) {
                return ResponseEntity.notFound().build();
            }
            if (!volunteerRepository.existsById(actionPrefRequest.volunteerId())) {
                return ResponseEntity.notFound().build();
            }
            scheduleService.choosePref(actionId, actionPrefRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{year}/{week}/actions/{actionId}")
    public ResponseEntity<?> chooseNeed(@PathVariable("year") int year, @PathVariable("week") int week, @PathVariable("actionId") Long actionId, @RequestBody ActionNeedRequest actionNeedRequest) {
        try {
            if (!actionRepository.existsById(actionId)) {
                return ResponseEntity.notFound().build();
            }
            if (!volunteerRepository.existsByVolunteerIdAndRole(actionNeedRequest.getLeaderId(), VolunteerRole.LEADER) ) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if(!Objects.equals(actionRepository.findById(actionId).get().getLeader().leaderId(), actionNeedRequest.getLeaderId())){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            scheduleService.scheduleNeedAction(actionId, year, week, actionNeedRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{year}/{week}/volunteers/{volunteerId}")
    public ResponseEntity<?> chooseAvail(@PathVariable("year") int year, @PathVariable("week") int week, @PathVariable("volunteerId") Long volunteerId, @RequestBody VolunteerAvailRequest volunteerAvailRequest) {
        try {
            if(!volunteerRepository.existsById(volunteerId)) {
                return ResponseEntity.notFound().build();
            }
            scheduleService.chooseAvailabilities(volunteerId, year, week, volunteerAvailRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{year}/{week}/schedule/generate")
    public ResponseEntity<?> generateSchedule(@PathVariable("year") int year, @PathVariable("week") int week, @RequestBody GenerateScheduleRequest generateScheduleRequest) {
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
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
            }

            // Wywołanie usługi generowania harmonogramu
            scheduleService.generateSchedule(generateScheduleRequest.date());
            return ResponseEntity.ok().body("Schedule generated successfully.");
        } catch (Exception e) {
            // Obsługa wyjątków i zwrócenie odpowiedzi z kodem błędu 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating schedule.");
        }
    }



    @PostMapping("/actions/{actionId}")
    public ResponseEntity<?> getScheduleByAction(@PathVariable("actionId") Long actionId, @RequestBody ScheduleByActionRequest scheduleByActionRequest) {
        try {
            if (!actionRepository.existsById(actionId)) {
                return ResponseEntity.notFound().build();
            }
            if (!volunteerRepository.existsByVolunteerIdAndRole(scheduleByActionRequest.leaderId(), VolunteerRole.LEADER) ) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if(!Objects.equals(actionRepository.findById(actionId).get().getLeader().leaderId(), scheduleByActionRequest.leaderId())){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            ActionScheduleDto scheduleDto = scheduleService.getActionSchedule(actionId);
            return ResponseEntity.ok(scheduleDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving action schedule.");
        }
    }

}



