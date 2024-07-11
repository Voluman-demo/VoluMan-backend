package com.example.demo.Interval;

import com.example.demo.Volunteer.Duty;
import com.example.demo.action.Demand;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "duty_interval")
public class DutyInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_id")
    private Long intervalId;

    private Timestamp startTime;
    private Timestamp endTime;

    private String status;

    private Long assign;


    @ManyToOne
    @JoinColumn(name = "duty_id")
    private Duty duty;
}