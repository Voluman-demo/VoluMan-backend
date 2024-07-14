package com.example.demo.Interval;


import com.example.demo.action.demand.Demand;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "demand_interval")
public class DemandInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_id")
    private Long intervalId;

    private LocalTime startTime;
    private LocalTime endTime;

    private Long needMin;
    private Long needMax;

    private Long currentVolunteers;

    @ManyToOne
    @JoinColumn(name = "demand_id")
    @JsonBackReference // Odwrotny odnośnik, ignorowany przy serializacji
    private Demand demand;

    @PrePersist
    public void prePersist() {
        if(this.currentVolunteers == null){
            this.currentVolunteers = 0L;
        }
    }
}
