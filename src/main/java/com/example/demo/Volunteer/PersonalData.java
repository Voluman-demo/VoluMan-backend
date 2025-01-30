package com.example.demo.Volunteer;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Data
@MappedSuperclass
public class PersonalData {

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "phone", length = 19)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 50)
    private String address;

    @Column(name = "sex", length = 1)
    private String sex;
}
