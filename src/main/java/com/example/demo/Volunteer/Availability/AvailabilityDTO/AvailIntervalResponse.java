package com.example.demo.Volunteer.Availability.AvailabilityDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AvailIntervalResponse {
    private Long intervalId;
    private String startTime;
    private String endTime;
}