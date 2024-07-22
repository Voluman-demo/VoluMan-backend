package com.example.demo.Volunteer.Duty.DutyInterval;

import com.example.demo.Action.ActionDto.ActionDto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DutyIntervalDto(
        Long intervalId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        ActionDto action
) {}
