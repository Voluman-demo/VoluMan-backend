package com.example.demo.action;

import com.example.demo.Preferences.Preferences;
import com.example.demo.Volunteer.Volunteer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
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
    private LocalDateTime beg;

    @Column(name = "end_time")
    private LocalDateTime end;

    private Long needMin;
    private Long needMax;
    private Long leaderId; //TODO pomyslec

    @ManyToMany(mappedBy = "actions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Volunteer> volunteers = new HashSet<>();

    @ManyToMany(mappedBy = "actions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Volunteer> determined = new HashSet<>();

    @ManyToMany(mappedBy = "T", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Preferences> preferencesT = new HashSet<>();

    @ManyToMany(mappedBy = "R", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Preferences> preferencesR = new HashSet<>();

    @ManyToMany(mappedBy = "N", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Preferences> preferencesN = new HashSet<>();
}
