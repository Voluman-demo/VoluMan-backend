package com.example.demo.Preferences;

import com.example.demo.action.Action;
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
    private Long preferenceId;

 /*   @OneToMany(mappedBy = "preferences", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Action> T = new ArrayList<>();

    @OneToMany(mappedBy = "preferences", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Action> R = new ArrayList<>();

    @OneToMany(mappedBy = "preferences", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Action> N = new ArrayList<>();*/

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "preferences_action",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> T = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "preferences_action",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> R = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "preferences_action",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> N = new HashSet<>();

}
