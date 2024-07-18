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

    @Column(name = "start_time",nullable = false)
    private LocalTime startTime;
    @Column(name = "end_time",nullable = false)
    private LocalTime endTime;
    //TODO DutyStatus
    private String status;

//    @Column(name = "assign",nullable = false,length = 4)
//    private Long assign;


    @ManyToOne
    @JoinColumn(name = "duty_id")
    @JsonBackReference
    private Duty duty;

//    @PrePersist
//    public void prePersist() {
//        if(this.assign == null){
//            this.assign = 0L;
//        }
//    }
}