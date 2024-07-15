package com.example.demo.Volunteer;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "volunteer_details")
public class VolunteerDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long volunteerId;

    private String name;
    private String lastname;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String city;
    private String street;
    private String houseNumber;
    private String apartmentNumber;
    private String postalNumber;
    private String sex;
}
