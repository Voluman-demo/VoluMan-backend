package com.example.demo.Action.ActionDemand.ActionDemandInterval;


import com.example.demo.Volunteer.VolunteerDto.VolunteerDto;

import java.time.LocalTime;
import java.util.List;


public record ActionDemandIntervalDto(
        Long intervalId,
        LocalTime startTime,
        LocalTime endTime,
        List<VolunteerDto> assignedVolunteers
) {}
