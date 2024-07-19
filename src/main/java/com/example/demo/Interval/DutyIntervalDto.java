package com.example.demo.Interval;

import com.example.demo.action.Dto.ActionDto;

import java.time.LocalTime;

public record DutyIntervalDto(
        Long intervalId,
        LocalTime startTime,
        LocalTime endTime,
        ActionDto action
) {}
