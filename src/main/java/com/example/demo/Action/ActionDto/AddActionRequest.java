package com.example.demo.Action.ActionDto;

import com.example.demo.Action.ActionStatus;

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

/*
{
    "adminId": 1,
    "heading": "tytul_akcji",
    "description": "opis_akcji",
    "status": "OPEN",
    "startDay": "2024-07-10",
    "endDay": "2024-07-14",
    "leaderId": 2
}
 */