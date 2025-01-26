package com.example.demo.Volunteer;

import com.example.demo.Action.Action;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Duty.Duty;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.Preferences.Preferences;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "volunteers")
@AttributeOverride(name = "email", column = @Column(name = "email", unique = true, nullable = false, length = 50))
public class Volunteer extends PersonalData {
    @EmbeddedId
    private ID volunteerId;

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
    @JoinColumn(name = "preferences_id")
    private Preferences preferences;

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    private ArrayList<Availability> availabilities;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "volunteers_actions",
            joinColumns = @JoinColumn(name = "volunteer_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> actions;

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Duty> duties;

    public Volunteer() {
        this.limitOfWeeklyHours = 0.0;
        this.actualWeeklyHours = 0.0;
        this.preferences = new Preferences();
        this.availabilities = new ArrayList<>();
        this.actions = new HashSet<>();
        this.duties = new HashSet<>();
    }

    public double calculateActualWeeklyHours(LocalDate startOfWeek, LocalDate endOfWeek) {
        this.actualWeeklyHours = duties.stream()
                .filter(duty -> !duty.getDate().isBefore(startOfWeek) && !duty.getDate().isAfter(endOfWeek))
                .mapToDouble(Duty::getTotalDurationHours)
                .sum();
        return this.actualWeeklyHours;
    }
}
