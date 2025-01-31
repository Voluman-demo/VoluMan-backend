package com.example.demo.Volunteer.Availability.AvailabilityDTO;

import lombok.Data;

import java.util.List;

@Data
public class SetAvailRequest {
    List<AvailabilityRequest> availabilities;
}

