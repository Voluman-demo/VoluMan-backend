//package com.example.demo.Volunteer.Duty.DutyInterval;
//
//import com.example.demo.Model.ID;
//import com.example.demo.Volunteer.Duty.Duty;
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalTime;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Table(name = "duty_interval")
//public class DutyInterval {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "interval_id")
//    private ID intervalId;
//
//    @Column(name = "start_time",nullable = false)
//    private LocalTime startTime;
//    @Column(name = "end_time",nullable = false)
//    private LocalTime endTime;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private DutyIntervalStatus status;
//
//
//    @ManyToOne
//    @JoinColumn(name = "duty_id")
//    @JsonBackReference
//    private Duty duty;
//
//}