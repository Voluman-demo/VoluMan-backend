package com.example.demo.Interval;

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

    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "availability_id")
    @JsonBackReference // Zarządzany odnośnik dla serializacji
    private Availability availability;
}
