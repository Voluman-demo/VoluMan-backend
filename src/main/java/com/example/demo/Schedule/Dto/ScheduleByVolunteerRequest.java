package com.example.demo.Schedule.Dto;

import java.time.LocalDate;

public record ScheduleByVolunteerRequest(
        LocalDate date
) {}
