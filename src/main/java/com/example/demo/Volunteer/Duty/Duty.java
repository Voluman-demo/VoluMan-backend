//package com.example.demo.Volunteer.Duty;
//
//import com.example.demo.Model.ID;
//import com.example.demo.Schedule.Schedule;
//import com.example.demo.Volunteer.Duty.DutyInterval.DutyInterval;
//import com.example.demo.Volunteer.Duty.DutyInterval.DutyIntervalStatus;
//import com.example.demo.Volunteer.Volunteer;
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.util.HashSet;
//import java.util.Set;
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Table(name = "duty")
//public class Duty {
//    @EmbeddedId
//    private ID dutyId;
//
//    @Column(name = "date", nullable = false)
//    private LocalDate date;
//
//    @ManyToOne
//    @JoinColumn(name = "schedule_id", nullable = false)
//    private Schedule schedule;
//
//    @ManyToOne
//    @JoinColumn(name = "volunteer_id", nullable = false)
//    private Volunteer volunteer;
//
//    @OneToMany(mappedBy = "duty", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<DutyInterval> dutyIntervals = new HashSet<>();
//
//    public double getTotalDurationHours() {
//        return dutyIntervals.stream()
//                .filter(interval -> interval.getStatus() == DutyIntervalStatus.ASSIGNED)
//                .mapToDouble(interval -> Duration.between(interval.getStartTime(), interval.getEndTime()).toMinutes() / 60.0)
//                .sum();
//    }
//}