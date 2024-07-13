package com.example.demo.Schedule;

import java.time.LocalDate;

public record GenerateScheduleRequest (
        Long adminId,
        LocalDate date
){}
