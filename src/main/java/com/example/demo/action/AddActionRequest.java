package com.example.demo.action;

import java.time.LocalDate;

public record AddActionRequest(
        Long adminId,
        String heading,
        String description,
        ActionStatus status,
        LocalDate startDay,
        LocalDate endDay,
        Long leaderId
) {}
