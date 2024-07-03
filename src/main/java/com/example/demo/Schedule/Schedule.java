package com.example.demo.Schedule;

import com.example.demo.Preferences.Preferences;
import com.example.demo.action.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "schedule")
public class Schedule {
    @Id
    private Long scheduleId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", referencedColumnName = "id")
    private Action action;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pref_id", referencedColumnName = "id")
    private Preferences preferences;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "lim_id", referencedColumnName = "id")
//    private Lim limits;


}
