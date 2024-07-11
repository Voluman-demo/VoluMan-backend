package com.example.demo.action;

import com.example.demo.Preferences.Preferences;
import com.example.demo.Volunteer.LeaderDto;
import com.example.demo.Volunteer.Volunteer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "action")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Long actionId;

    private String heading;
    private String description;
    private ActionStatus status;

    private LocalDate startDay;
    private LocalDate endDay;

    @Embedded
    private LeaderDto leader;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Demand> demands;

    @ManyToMany(mappedBy = "actions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Volunteer> volunteers = new HashSet<>(); //T/R

    @ManyToMany(mappedBy = "actions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Volunteer> determined = new HashSet<>(); //T

//    @ManyToMany(mappedBy = "T", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private Set<Preferences> preferencesT = new HashSet<>();
//
//    @ManyToMany(mappedBy = "R", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private Set<Preferences> preferencesR = new HashSet<>();
//
//    @ManyToMany(mappedBy = "N", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private Set<Preferences> preferencesN = new HashSet<>();
}
