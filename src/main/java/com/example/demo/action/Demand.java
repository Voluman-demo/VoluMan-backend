package com.example.demo.action;

import com.example.demo.Interval.DemandInterval;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "demand")
public class Demand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "demand_id")
    private Long demandId;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;


    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL, orphanRemoval = true) //TODO
    private Set<DemandInterval> demandIntervals = new HashSet<>();

}