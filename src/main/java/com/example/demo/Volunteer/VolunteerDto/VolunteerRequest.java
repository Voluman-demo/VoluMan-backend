package com.example.demo.Volunteer.VolunteerDto;

import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Position.Position;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;


@Data
public class VolunteerRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
    private String sex;
    private Position position;
    private double limitOfWeeklyHours;
    private ArrayList<Availability> availabilities;
}

