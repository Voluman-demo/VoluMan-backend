//package com.example.demo.Schedule;
//
//import com.example.demo.Volunteer.Volunteer;
//import com.example.demo.action.Action;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.List;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Table(name = "schedule")
//public class Schedule {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "schedule_id")
//    private Long scheduleId;
//
//
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "action_id", referencedColumnName = "action_id")
//    private Action action;
//
//}