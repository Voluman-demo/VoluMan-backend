package com.example.demo.Schedule.ScheduleDto;

import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Duty.DutyInterval.DutyIntervalDto;

import java.util.List;

public record VolunteerScheduleDto(
        ID volunteerId,
        String name,
        String lastname,
        List<DutyIntervalDto> dutyIntervals
) {}
