package com.example.demo.Action.ActionDemand;

import com.example.demo.Action.ActionDemand.ActionDemandInterval.ActionDemandInterval;
import com.example.demo.Action.Action;
import com.example.demo.Schedule.Schedule;
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
@Table(name = "action_demand")
public class ActionDemand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "demand_id")
    private Long demandId;

    @Column(name = "date",nullable = false)
    private LocalDate date;


    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;


    @OneToMany(mappedBy = "actionDemand", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ActionDemandInterval> actionDemandIntervals = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

}