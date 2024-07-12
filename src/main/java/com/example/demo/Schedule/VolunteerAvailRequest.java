package com.example.demo.Schedule;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class VolunteerAvailRequest {
    private Long limitOfHours;
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


