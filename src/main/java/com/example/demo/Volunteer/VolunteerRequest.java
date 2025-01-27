package com.example.demo.Volunteer;

import com.example.demo.Volunteer.Position.Position;
import lombok.Data;

import java.time.LocalDate;


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
}
