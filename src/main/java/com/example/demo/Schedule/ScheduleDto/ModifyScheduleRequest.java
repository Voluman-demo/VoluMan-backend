package com.example.demo.Schedule.ScheduleDto;



import java.util.List;

public record ModifyScheduleRequest(
        Long volunteerId,
        List<Long> demandIntervalIds,
        ModificationType modificationType
) {}

