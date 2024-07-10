package com.example.demo.action;

import java.time.LocalDateTime;

public record AddActionRequest(
        Long adminId,
        String heading,
        String description,
        ActionStatus status,
        LocalDateTime beg,
        LocalDateTime end,
        Long needMin,
        Long needMax,
        Long leaderId

) {
}
