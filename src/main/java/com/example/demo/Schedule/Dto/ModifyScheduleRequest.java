package com.example.demo.Schedule.Dto;


import com.example.demo.Interval.DutyInterval;

import java.util.List;

public record ModifyScheduleRequest(
    Long actionId,
    List<DutyInterval> dutyIntervals
) {}
