package com.example.demo.Volunteer.VolunteerDto;

import com.example.demo.Model.ID;

public record VolunteerDto(
        ID volunteerId,
        String firstname,
        String lastname
) {}
