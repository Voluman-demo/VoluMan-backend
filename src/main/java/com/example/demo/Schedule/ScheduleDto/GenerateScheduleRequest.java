package com.example.demo.Schedule.ScheduleDto;

import java.time.LocalDate;

public record GenerateScheduleRequest (
        Long adminId,
        LocalDate date
){}
