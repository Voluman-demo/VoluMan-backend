package com.example.demo.Candidate;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long candidateId;

    private String name;
    private String lastname;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private String street;
    private String city;
    private String houseNumber;
    private String apartmentNumber;
    private String postalNumber;
    private String sex;
}
