package com.example.demo.Volunteer.Availability.AvailabilityDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AvailResponse {
    private Long availabilityId;
    private Long volunteerId;
    private LocalDate date;
    private List<AvailIntervalResponse> slots;
}


