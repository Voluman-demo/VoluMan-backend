package com.example.demo.Schedule.Dto;

import com.example.demo.Interval.DutyIntervalDto;

import java.util.List;

public record VolunteerScheduleDto(
        Long volunteerId,
        String name,
        String lastname,
        List<DutyIntervalDto> dutyIntervals
) {}
