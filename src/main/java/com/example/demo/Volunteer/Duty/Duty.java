package com.example.demo.Volunteer.Duty;

import com.example.demo.Interval.DutyInterval;
import com.example.demo.Volunteer.Volunteer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "duty")
public class Duty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "demand_id")
    private Long demandId;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    @JsonBackReference
    private Volunteer volunteer;


    @OneToMany(mappedBy = "duty", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DutyInterval> dutyIntervals = new HashSet<>();


    public long getTotalDurationHours() {
        return dutyIntervals.stream()
                .mapToLong(interval -> Duration.between(interval.getStartTime(), interval.getEndTime()).toHours())
                .sum();
    }
}