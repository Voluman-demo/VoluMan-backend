package com.example.demo.Log;

import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LogService {
    private final LogRepository logRepository;
    private final VolunteerRepository volunteerRepository;

    public LogService(LogRepository logRepository, VolunteerRepository volunteerRepository) {
        this.logRepository = logRepository;
        this.volunteerRepository = volunteerRepository;
    }

    private LogResult log(LogUserDto logUserDto, EventType eventType, String eventDesc) {
        try {
            Log log = new Log();
            log.setFirstName(logUserDto.firstName());
            log.setLastName(logUserDto.lastName());
            log.setEmail(logUserDto.email());
            log.setEventType(eventType);
            log.setEventDesc(eventDesc);
            log.setTimestamp(LocalDateTime.now());

            logRepository.save(log);
            return new LogResult(true, null);
        } catch (Exception e) {
            return new LogResult(false, e.getMessage());
        }
    }

    public void logCandidate(Volunteer candidate, EventType eventType, String eventDesc) {
        try {

            LogResult result = log(
                    new LogUserDto(candidate.getFirstName(), candidate.getLastName(), candidate.getEmail()),
                    eventType,
                    eventDesc
            );

            if (!result.isSuccess()) {
                throw new RuntimeException(result.getErrorMessage());
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logVolunteer(Volunteer volunteer, EventType eventType, String eventDesc) {
        try {
            LogResult result = log(
                    new LogUserDto(
                            volunteer.getFirstName(),
                            volunteer.getLastName(),
                            volunteer.getEmail()
                    ),
                    eventType,
                    eventDesc
            );

            if (!result.isSuccess()) {
                throw new RuntimeException(result.getErrorMessage());
            }
        } catch (RuntimeException e) {
            System.out.println("Error during logging: " + e.getMessage());
        }
    }


    public void logAction(Long volunteerId, EventType eventType, String eventDesc) {
        try {
            Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);


            LogResult result = log(
                    new LogUserDto(
                            volunteer.get().getFirstName(),
                            volunteer.get().getLastName(),
                            volunteer.get().getEmail()
                    ),
                    eventType,
                    eventDesc
            );

            if (!result.isSuccess()) {
                throw new RuntimeException(result.getErrorMessage());
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logSchedule(Long volunteerId, EventType eventType, String eventDesc) {
        try {
            Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);

            LogResult result = log(
                    new LogUserDto(
                            volunteer.get().getFirstName(),
                            volunteer.get().getLastName(),
                            volunteer.get().getEmail()
                    ),
                    eventType,
                    eventDesc
            );

            if (!result.isSuccess()) {
                throw new RuntimeException(result.getErrorMessage());
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
