package com.example.demo.Schedule.ScheduleDto;

import com.example.demo.Model.ID;

import java.time.LocalDate;

public record GenerateScheduleRequest (
        ID adminId,
        LocalDate date
){}
