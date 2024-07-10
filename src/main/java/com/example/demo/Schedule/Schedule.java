package com.example.demo.Schedule;

import com.example.demo.Preferences.Preferences;
import com.example.demo.action.Action;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", referencedColumnName = "action_id")
    private Action action;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "pref_id", referencedColumnName = "id")
//    private Preferences preferences;

}