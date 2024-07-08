package com.example.demo.Preferences;

import com.example.demo.action.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "preferences")
public class Preferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferenceId;

    @OneToMany(mappedBy = "preferences", cascade = CascadeType.ALL)
    private List<Action> T;

    @OneToMany(mappedBy = "preferences", cascade = CascadeType.ALL)
    private List<Action> R;

    @OneToMany(mappedBy = "preferences", cascade = CascadeType.ALL)
    private List<Action> N;

}
