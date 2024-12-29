package com.example.demo.Volunteer.Availability.AvailabilityDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class VolunteerAvailRequest {

    private List<DayAvailabilityRequest> days;

    @Getter
    @Setter
    public static class DayAvailabilityRequest {
        private LocalDate date;
        private List<AvailabilitySlotRequest> slots;
    }

    @Getter
    @Setter
    public static class AvailabilitySlotRequest {
        private LocalTime startTime;
        private LocalTime endTime;
    }
}


/*
POST: http://localhost:8080/schedules/2024/28/volunteers/3
{
    "limitOfHours": 8,
    "days": [
        {
            "date": "2024-07-13",
            "slots": [
                {
                    "startTime": "09:00:00",
                    "endTime": "09:30:00"
                },
                {
                    "startTime": "09:30:00",
                    "endTime": "10:00:00"
                }
            ]
        },
        {
            "date": "2024-07-13",
            "slots": [
                {
                    "startTime": "10:00:00",
                    "endTime": "10:30:00"
                },
                {
                    "startTime": "10:30:00",
                    "endTime": "11:00:00"
                }
            ]
        }
    ]
}
 */