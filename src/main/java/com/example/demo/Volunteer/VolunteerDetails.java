package com.example.demo.Volunteer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "volunteer_details")
public class VolunteerDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long volunteerId;

    private String name;
    private String lastname;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private String city;
    private String street;
    private String houseNumber;
    private String apartmentNumber;
    private String postalNumber;
    private String sex;
}
