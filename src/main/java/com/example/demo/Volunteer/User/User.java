package com.example.demo.Volunteer.User;


import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Volunteer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID userId;

    @Column(unique = true)
    private String email;

    private String password;

    private LocalDate dateOfChangePassword;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "volunteer_id", referencedColumnName = "id")
    @JsonIgnore
    private Volunteer volunteer;

    public User(ID userId) {
        this.userId = userId;
    }
}

