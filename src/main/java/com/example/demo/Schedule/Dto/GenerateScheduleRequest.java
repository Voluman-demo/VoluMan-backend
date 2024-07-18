package com.example.demo.Schedule.Dto;

import java.time.LocalDate;

public record GenerateScheduleRequest (
        Long adminId,
        LocalDate date
){}
