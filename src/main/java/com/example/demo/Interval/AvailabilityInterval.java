package com.example.demo.Interval;

import com.example.demo.Volunteer.Availability;
import com.example.demo.Volunteer.Volunteer;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "time_interval") // Changed table name
public class Interval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_id")
    private Long intervalId;

    private Timestamp startTime;
    private Timestamp endTime;

    @ManyToOne
    @JoinColumn(name = "availability_id")
    private Availability availability;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;
}
