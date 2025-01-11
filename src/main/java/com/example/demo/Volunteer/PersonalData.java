package com.example.demo.Volunteer;

import com.example.demo.Model.ID;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Data
public class PersonalData {

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 50)
    private String email;

    @Column(name = "phone", nullable = false, length = 19)
    private String phone;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "address", nullable = false, length = 50)
    private String address;

    @Column(name = "sex", length = 1)
    private String sex;
}
