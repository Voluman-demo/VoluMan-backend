package com.example.demo.action.demand;

import com.example.demo.Interval.DemandInterval;
import com.example.demo.action.Action;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "demand")
public class Demand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "demand_id")
    private Long demandId;

    private LocalDate date;


    @ManyToOne
    @JoinColumn(name = "action_id")
    @JsonBackReference // Odwrotny odnośnik, ignorowany przy serializacji
    private Action action;


    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Zarządzany odnośnik dla serializacji
    private Set<DemandInterval> demandIntervals = new HashSet<>();

}