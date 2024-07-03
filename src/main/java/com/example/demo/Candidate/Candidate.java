package com.example.demo.Candidate;

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
    private String houseNumber;
    private String apartmentNumber;
    private String postalNumber;
    private String sex;
}
