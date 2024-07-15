package com.example.demo.Schedule;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ActionNeedRequest {
    private Long leaderId;
    private List<DayRequest> days;

    @Getter
    @Setter
    public static class DayRequest {
        private LocalDate date;
        private List<SlotRequest> slots;

    }

    @Getter
    @Setter
    public static class SlotRequest {
        private LocalTime startTime;
        private LocalTime endTime;
        private Long needMin;
        private Long needMax;

    }
}