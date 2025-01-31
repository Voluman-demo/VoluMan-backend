package com.example.demo.Volunteer;

import com.example.demo.Action.Action;
import com.example.demo.Action.ActionDemand.ActionDemandInterval.ActionDemandInterval;
import com.example.demo.Action.Lang;
import com.example.demo.Schedule.Schedule;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.Preferences.Preferences;
import com.example.demo.Volunteer.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "volunteers")
public class Volunteer extends PersonalData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "volunteer_id")
    private Long volunteerId;

    @Column(name = "valid", nullable = false)
    private boolean valid = false; //TODO usunac

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Lang language;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private Position position;

    @Column(name = "limit_of_weekly_hours", nullable = false, length = 3)
    private double limitOfWeeklyHours;

    @Column(name = "actual_weekly_hours", nullable = false, length = 3)
    private double actualWeeklyHours;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "preferences_id")
    @JsonManagedReference
    private Preferences preferences;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    private User user;

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Availability> availabilities;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "volunteers_actions",
            joinColumns = @JoinColumn(name = "volunteer_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    @JsonIgnore
    private Set<Action> actions;


    @ManyToMany(mappedBy = "assignedVolunteers", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonIgnore
    private Set<ActionDemandInterval> assignedActionDemandIntervals = new HashSet<>();

    @ManyToMany(mappedBy = "volunteers", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Schedule> schedules = new ArrayList<>();

    public Volunteer() {
        this.setFirstName("");
        this.setLastName("");
        this.setEmail("");
        this.setPhone("");
        this.setDateOfBirth(LocalDate.now());
        this.setAddress("");
        this.setSex("");
        this.limitOfWeeklyHours = 0.0;
        this.actualWeeklyHours = 0.0;
        this.position = Position.CANDIDATE;
        this.language = Lang.PL;
    }


    public double calculateActualWeeklyHours(LocalDate startOfWeek, LocalDate endOfWeek) {
        this.actualWeeklyHours = assignedActionDemandIntervals.stream()
                .filter(interval -> {
                    LocalDate intervalDate = interval.getActionDemand().getDate();
                    return !intervalDate.isBefore(startOfWeek) && !intervalDate.isAfter(endOfWeek);
                })
                .mapToDouble(interval -> Duration.between(interval.getStartTime(), interval.getEndTime()).toMinutes() / 60.0)
                .sum();

        return this.actualWeeklyHours;
    }
}