package com.example.demo.Schedule.ScheduleDto;


import com.example.demo.Volunteer.Duty.DutyInterval.DutyInterval;

import java.util.List;

public record ModifyScheduleRequest(
    Long actionId,
    List<DutyInterval> dutyIntervals
) {}
