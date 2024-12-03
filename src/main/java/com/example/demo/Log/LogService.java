package com.example.demo.Log;

import com.example.demo.Volunteer.Candidate.Candidate;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDetails;
import com.example.demo.Volunteer.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class LogService {
    private final LogRepository logRepository;
    private final VolunteerRepository volunteerRepository;

    public LogService(LogRepository logRepository, VolunteerRepository volunteerRepository) {
        this.logRepository = logRepository;
        this.volunteerRepository = volunteerRepository;
    }

    private Error log(LogUserDto logUserDto, EventType eventType, String eventDesc) {
        try {
            Log log = new Log();
            log.setFirstName(logUserDto.firstName());
            log.setLastName(logUserDto.lastName());
            log.setEmail(logUserDto.email());
            log.setEventType(eventType);
            log.setEventDesc(eventDesc);
            log.setTimestamp(LocalDateTime.now());

            logRepository.save(log);
            return null;
        }catch (Exception e){
            return new Error(e.getMessage());
        }
    }

    public void logCandidate(Candidate candidate, EventType eventType, String eventDesc) {
        try{

            String error = log(
                    new LogUserDto(candidate.getFirstname(), candidate.getLastname(), candidate.getEmail()),
                    eventType,
                    eventDesc
            ).getMessage();

            if (error != null) {
                throw new RuntimeException(error);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logVolunteer(Volunteer volunteer, EventType eventType, String eventDesc) {
        try{
            String error = Objects.requireNonNull(log(
                    new LogUserDto(
                            volunteer.getVolunteerDetails().getFirstname(),
                            volunteer.getVolunteerDetails().getLastname(),
                            volunteer.getVolunteerDetails().getEmail()
                    ),
                    eventType,
                    eventDesc
            )).getMessage();

            if (error != null) {
                throw new RuntimeException(error);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }


    public void logAction(Long volunteerId, EventType eventType, String eventDesc) {
        try {
            Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
            VolunteerDetails volunteerDetails = volunteer.get().getVolunteerDetails();


            String error = log(
                    new LogUserDto(
                            volunteerDetails.getFirstname(),
                            volunteerDetails.getLastname(),
                            volunteerDetails.getEmail()
                    ),
                    eventType,
                    eventDesc
            ).getMessage();

            if (error != null) {
                throw new RuntimeException(error);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logSchedule(Long volunteerId, EventType eventType, String eventDesc) {
        try {
            Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
            VolunteerDetails volunteerDetails = volunteer.get().getVolunteerDetails();

            String error = log(
                    new LogUserDto(
                            volunteerDetails.getFirstname(),
                            volunteerDetails.getLastname(),
                            volunteerDetails.getEmail()
                    ),
                    eventType,
                    eventDesc
            ).getMessage();

            if (error != null) {
                throw new RuntimeException(error);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
