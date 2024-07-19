package com.example.demo.Interval;

import com.example.demo.action.Dto.ActionDto;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;

public record DutyIntervalDto(
        Long intervalId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        ActionDto action
) {}
