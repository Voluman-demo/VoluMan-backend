package com.example.demo.Volunteer.Availability;

import com.example.demo.Log.LogService;
import com.example.demo.Volunteer.VolunteerRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class AvailabilityController {
    private final AvailabilityRepository availabilityRepository;
    private final VolunteerRepository volunteerRepository;
    private final LogService logService;
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityRepository availabilityRepository, VolunteerRepository volunteerRepository, LogService logService, AvailabilityService availabilityService) {
        this.availabilityRepository = availabilityRepository;
        this.volunteerRepository = volunteerRepository;
        this.logService = logService;
        this.availabilityService = availabilityService;
    }
//
//
//    @GetMapping("/volunteers/availabilities")
//    public ResponseEntity<?> getAllAvailability() {
//        List<Availability> availabilities = availabilityRepository.findAll();
//        List<AvailResponse> volunteerAvailResponses = availabilityService.getAllAvailability(availabilities);
//        return ResponseEntity.ok(volunteerAvailResponses);
//    }
//
//    @GetMapping("/volunteers/{volunteerId}/availabilities")
//    public ResponseEntity<?> getAvailability(@PathVariable ID volunteerId) {
//        try {
//            if (!volunteerRepository.existsById(volunteerId)) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Volunteer not found.");
//            }
//
//            List<Availability> availabilities = availabilityRepository.findAllByVolunteer_VolunteerId(volunteerId);
//
//            if (availabilities.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No availabilities found for this volunteer.");
//            }
//
//            VolunteerAvailResponse response = new VolunteerAvailResponse(volunteerId, availabilities);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
   /* @PutMapping("/volunteers/{idVolunteer}/limit-weekly-hours")
    public ResponseEntity<Void> setWeekHourLim(@PathVariable ID idVolunteer, @RequestParam ID limitOfHours){
        if(!volunteerRepository.existsByVolunteerId(idVolunteer)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Volunteer> volunteer = volunteerRepository.findById(idVolunteer);

        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            vol.setLimitOfWeeklyHours(limitOfHours);
            volunteerRepository.save(vol);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }*/
//    @PutMapping("/volunteers/{idVolunteer}/limit-weekly-hours")
//    public ResponseEntity<Void> setWeekHourLim(@PathVariable ID idVolunteer, @RequestBody limitOfHoursRequest limitOfHoursRequest){
//        if(volunteerRepository.existsById(idVolunteer)){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        availabilityService.setWeekHourLim(idVolunteer, limitOfHoursRequest);
//        return ResponseEntity.ok().build();
//    }
//    @GetMapping("/volunteers/{idVolunteer}/limit-weekly-hours")
//    public ResponseEntity<WeeklyHourLimitResponse> getWeekHourLim(@PathVariable ID idVolunteer) {
//        if (!volunteerRepository.existsById(idVolunteer)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        Double limitOfWeeklyHours = availabilityService.getWeekHourLim(idVolunteer);
//        WeeklyHourLimitResponse response = new WeeklyHourLimitResponse(limitOfWeeklyHours);
//        return ResponseEntity.ok(response);
//    }
//
//
//
//
//    @PostMapping("/volunteers/{volunteerId}/availabilities")
//    public ResponseEntity<?> chooseAvail(
//            @PathVariable ID volunteerId,
//            @RequestParam(defaultValue = "0") int year,
//            @RequestParam(defaultValue = "0") int week,
//            @RequestBody VolunteerAvailRequest volunteerAvailRequest
//    ) {
//        try {
//            if (!volunteerRepository.existsById(volunteerId)) {
//                return ResponseEntity.notFound().build();
//            }
//            availabilityService.chooseAvailabilities(volunteerId, year, week, volunteerAvailRequest);
//
//            logService.logSchedule(volunteerId, EventType.UPDATE, "Choose availabilities");
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

}
