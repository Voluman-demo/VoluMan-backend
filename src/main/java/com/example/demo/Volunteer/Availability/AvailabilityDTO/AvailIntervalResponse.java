package com.example.demo.Volunteer.Availability.AvailabilityDTO;

import com.example.demo.Model.ID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AvailIntervalResponse {
    private ID intervalId;
    private String startTime;
    private String endTime;
}