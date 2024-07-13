package com.example.demo.Volunteer;

import com.example.demo.Preferences.Preferences;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Duty.Duty;
import com.example.demo.action.Action;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "volunteer")
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long volunteerId;

    @Enumerated(EnumType.STRING)
    private VolunteerRole role;

    private Long limitOfWeeklyHours;

    private Long currentWeeklyHours;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "volunteer_details_id", referencedColumnName = "volunteerId")
    @JsonIgnore // Ignoruj przy serializacji, aby uniknąć rekurencji
    private VolunteerDetails volunteerDetails;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "preferences_id", referencedColumnName = "preferenceId")
    @JsonIgnore // Ignoruj przy serializacji, aby uniknąć rekurencji
    private Preferences preferences = new Preferences();

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Zarządzany odnośnik dla serializacji
    private List<Availability> availabilities = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "volunteer_action",
            joinColumns = @JoinColumn(name = "volunteer_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    @JsonIgnore // Ignoruj przy serializacji, aby uniknąć rekurencji
    private Set<Action> actions = new HashSet<>(); //relacja

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Ignoruj przy serializacji, aby uniknąć rekurencji
    private Set<Duty> duties = new HashSet<>();

    public long calculateCurrentWeeklyHours(LocalDate startOfWeek, LocalDate endOfWeek) {
        return duties.stream()
                .filter(duty -> !duty.getDate().isBefore(startOfWeek) && !duty.getDate().isAfter(endOfWeek))
                .mapToLong(Duty::getTotalDurationHours)
                .sum();
    }

    @PrePersist
    public void prePersist() {
        if(this.currentWeeklyHours == null){
            this.currentWeeklyHours = 0L;
        }

        if(this.limitOfWeeklyHours == null){
            this.limitOfWeeklyHours = 0L;
        }

        if (this.preferences == null) {
            this.preferences = new Preferences();
        }
    }
}
