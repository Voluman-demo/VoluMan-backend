package com.example.demo.Interval;


import com.example.demo.Volunteer.Availability;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "interval")
public class Interval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "volunteer_id")
    private Long intervalId;

    private Timestamp startTime;
    private Timestamp endTime;

    @ManyToOne
    @JoinColumn(name = "availability_id")
    private Availability availability;
}
