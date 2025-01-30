package com.example.demo.Volunteer;

import com.example.demo.Volunteer.Availability.Availability;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SetAvailRequest {
    List<AvailabilityRequest> availabilities;




}

