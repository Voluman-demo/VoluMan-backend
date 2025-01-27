package com.example.demo.Action.Demand.DemandInterval;


import com.example.demo.Action.Demand.Demand;
import com.example.demo.Volunteer.Volunteer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "demand_interval")
public class DemandInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_id")
    private Long intervalId;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    @Column(name = "need_min", nullable = false, length = 4)
    private Long needMin;
    @Column(name = "need_max", nullable = false, length = 4)
    private Long needMax;


    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "demand_interval_volunteers",
            joinColumns = @JoinColumn(name = "interval_id"),
            inverseJoinColumns = @JoinColumn(name = "volunteer_id", referencedColumnName = "volunteer_id")
    )
    private Set<Volunteer> assignedVolunteers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "demand_id")
    @JsonBackReference
    private Demand demand;
}
//    @PrePersist
//    public void prePersist() {
//        if(this.currentVolunteersNumber == null){
//            this.currentVolunteersNumber = 0L;
//        }
//    }
//}
