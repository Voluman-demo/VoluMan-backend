package com.example.demo.Volunteer.Availability.AvailabilityDTO;

import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Availability.Availability;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class VolunteerAvailResponse {
    private ID volunteerId;
    private List<Availability> availabilities;
}
