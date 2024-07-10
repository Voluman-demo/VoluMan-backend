package com.example.demo.action;

import com.example.demo.Preferences.Preferences;
import com.example.demo.Volunteer.Volunteer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Action")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Long actionId;

    private String heading;
    private String description;
    private ActionStatus status;
    private LocalDateTime beg;

    @Column(name = "end_time")
    private LocalDateTime end;

    private Long needMin;
    private Long needMax;
    private Long leaderId;

    @ManyToMany(mappedBy = "actions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Volunteer> volunteers = new HashSet<>();

    @ManyToMany(mappedBy = "actions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Volunteer> determined;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferences_id", nullable = false)
    private Preferences preferences;
}
