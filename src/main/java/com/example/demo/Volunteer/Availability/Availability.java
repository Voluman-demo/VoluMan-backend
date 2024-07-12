package com.example.demo.Volunteer.Availability;


import com.example.demo.Interval.AvailabilityInterval;
import com.example.demo.Volunteer.Volunteer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    private Long availabilityId;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    @JsonBackReference // Odwrotny odnośnik, ignorowany przy serializacji
    private Volunteer volunteer;

    @OneToMany(mappedBy = "availability", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Zarządzany odnośnik dla serializacji
    private Set<AvailabilityInterval> slots;
}

