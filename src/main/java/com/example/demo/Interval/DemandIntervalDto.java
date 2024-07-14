package com.example.demo.Interval;

import com.example.demo.Volunteer.VolunteerDto;

import java.time.LocalTime;
import java.util.List;


public record DemandIntervalDto(
        Long intervalId,
        LocalTime startTime,
        LocalTime endTime,
        List<VolunteerDto> assignedVolunteers
) {}
