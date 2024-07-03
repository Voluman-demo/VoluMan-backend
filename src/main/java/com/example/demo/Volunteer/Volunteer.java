package com.example.demo.Volunteer;

import com.example.demo.Interval.Interval;
import com.example.demo.Preferences.Preferences;
import com.example.demo.action.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "volunteer")
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long volunteerId;

    private String role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "volunteer_details_id", referencedColumnName = "volunteerId")
    private VolunteerDetails volunteerDetails;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "preferences_id", referencedColumnName = "id")
    private Preferences preferences;


    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL)
    private List<Interval> availabilities;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "volunteer_action",
            joinColumns = @JoinColumn(name = "volunteer_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> actions = new HashSet<>();

}