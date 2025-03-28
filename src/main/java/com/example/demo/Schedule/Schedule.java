package com.example.demo.Schedule;

import com.example.demo.Action.Action;
import com.example.demo.Action.ActionDemand.ActionDemand;

import com.example.demo.Volunteer.Volunteer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "schedule_actions",
            joinColumns = @JoinColumn(name = "schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    @JsonIgnore
    private List<Action> actions = new ArrayList<>();

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "schedule_volunteers",
            joinColumns = @JoinColumn(name = "schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "volunteer_id")
    )
    @JsonIgnore
    private List<Volunteer> volunteers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "schedule")
    @JsonManagedReference
    private List<ActionDemand> actionDemands = new ArrayList<>();

    public Schedule(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate != null ? startDate : LocalDate.now();
        this.endDate = endDate != null ? endDate : LocalDate.now().plusDays(7);
    }
}
