package com.example.demo.Volunteer.Availability.AvailabilityDTO;

import com.example.demo.Model.ID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AvailResponse {
    private ID availabilityId;
    private ID volunteerId;
    private LocalDate date;
    private List<AvailIntervalResponse> slots;
}


