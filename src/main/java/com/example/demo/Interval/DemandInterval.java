package com.example.demo.Interval;


import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "action_interval") // Changed table name
public class ActionInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_id")
    private Long intervalId;

    private Timestamp startTime;
    private Timestamp endTime;

    private Long needMin;
    private Long needMax;

}
