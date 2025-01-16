package com.example.demo.Volunteer;

import com.example.demo.Action.Action;
import com.example.demo.Action.Version;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Duty.Duty;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.Preferences.Preferences;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "volunteer")
public class Volunteer extends PersonalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;

    @Column(name = "valid", nullable = false)
    private boolean valid = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private Position position;

    @Column(name = "limit_of_weekly_hours", nullable = false, length = 3)
    private double limitOfWeeklyHours;

    @Column(name = "actual_weekly_hours", nullable = false, length = 3)
    private double actualWeeklyHours;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "preferences_id", referencedColumnName = "preferenceId")
    private Preferences preferences;

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private ArrayList<Availability> availabilities;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "volunteer_action",
            joinColumns = @JoinColumn(name = "volunteer_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    @JsonIgnore
    private Set<Action> actions;

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Duty> duties;

    public Volunteer() {
        this.limitOfWeeklyHours = 0.0;
        this.actualWeeklyHours = 0.0;
        this.preferences = new Preferences();
        this.availabilities = new ArrayList<>();
        this.actions = new HashSet<>();
        this.duties = new HashSet<>();
    }

    @PrePersist
    public void prePersist() {
        if (this.preferences == null) {
            this.preferences = new Preferences();
        }

        if (this.actions == null) {
            this.actions = new HashSet<>();
        }

        if (this.duties == null) {
            this.duties = new HashSet<>();
        }

        if (this.availabilities == null) {
            this.availabilities = new ArrayList<>();
        }
    }

}
