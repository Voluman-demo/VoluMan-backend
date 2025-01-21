package com.example.demo.Schedule.ScheduleDto;


import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Duty.DutyInterval.DutyInterval;

import java.util.List;

public record ModifyScheduleRequest(
    ID volunteerId,
    List<DutyInterval> dutyIntervals
) {}
