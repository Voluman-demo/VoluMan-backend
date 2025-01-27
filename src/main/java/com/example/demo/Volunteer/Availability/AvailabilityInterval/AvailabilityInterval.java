package com.example.demo.Volunteer.Availability.AvailabilityInterval;

import com.example.demo.Volunteer.Availability.Availability;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Data
@Table(name = "availability_interval")
public class AvailabilityInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_id")
    private Long intervalId;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "availability_id")
    @JsonBackReference
    private Availability availability;
}
