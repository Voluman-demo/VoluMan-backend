package com.example.demo.Volunteer.Availability.AvailabilityDTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AvailabilityRequest {
    private LocalDate date;
    private List<IntervalRequest> slots;
}
