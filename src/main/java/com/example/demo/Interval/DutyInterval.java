package com.example.demo.Interval;

import com.example.demo.Volunteer.Duty.Duty;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "duty_interval")
public class DutyInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_id")
    private Long intervalId;

    private LocalTime startTime;
    private LocalTime endTime;

    private String status;

    private Long assign;


    @ManyToOne
    @JoinColumn(name = "duty_id")
    @JsonBackReference
    private Duty duty;

    @PrePersist
    public void prePersist() {
        if(this.assign == null){
            this.assign = 0L;
        }
    }
}