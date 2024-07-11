package com.example.demo.Volunteer;

import com.example.demo.Interval.DutyInterval;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "duty")
public class Duty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "demand_id")
    private Long demandId;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;


    @OneToMany(mappedBy = "duty", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DutyInterval> dutyIntervals = new HashSet<>();

}