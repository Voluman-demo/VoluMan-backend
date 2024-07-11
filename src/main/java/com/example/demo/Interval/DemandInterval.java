package com.example.demo.Interval;


import com.example.demo.action.Demand;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "demand_interval")
public class DemandInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_id")
    private Long intervalId;

    private Timestamp startTime;
    private Timestamp endTime;

    private Long needMin;
    private Long needMax;


    @ManyToOne
    @JoinColumn(name = "demand_id")
    private Demand demand;
}
