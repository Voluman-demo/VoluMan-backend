package com.example.demo.Action.Demand.DemandInterval;


import com.example.demo.Volunteer.VolunteerDto.VolunteerDto;

import java.time.LocalTime;
import java.util.List;


public record DemandIntervalDto(
        Long intervalId,
        LocalTime startTime,
        LocalTime endTime,
        List<VolunteerDto> assignedVolunteers
) {}
