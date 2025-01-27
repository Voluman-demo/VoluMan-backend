package com.example.demo.Volunteer.Preferences;

import com.example.demo.Action.Action;
import com.example.demo.Volunteer.Volunteer;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "preferences")
public class Preferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preference_id")
    private Long preferenceId;

    // Strongly Mine: Actions the volunteer strongly wants to participate in
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "preferences_action_s",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> S;

    // Weakly Mine: Actions the volunteer is willing to join if necessary
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "preferences_action_w",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> W;

    // Rejected: Actions the volunteer does not want to participate in
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "preferences_action_r",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> R;

    // Undecided: Actions the volunteer has not yet decided on
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "preferences_action_u",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> U;

    @OneToOne(mappedBy = "preferences")

    private Volunteer volunteer;

    @PrePersist
    public void initializeDefaults() {
        if (S == null) S = new HashSet<>();
        if (W == null) W = new HashSet<>();
        if (R == null) R = new HashSet<>();
        if (U == null) U = new HashSet<>();
    }
}

