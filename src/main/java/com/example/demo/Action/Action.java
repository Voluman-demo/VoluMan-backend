package com.example.demo.Action;

import com.example.demo.Volunteer.Volunteer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;


@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "actions")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Long actionId;

    @Column(name = "begin_date")
    private LocalDate begin;

    @Column(name = "end_date")
    private LocalDate end;

    @Column(name = "leader_id")
    private Long leaderId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "action", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Description> descr;

    @ManyToMany(mappedBy = "actions", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Volunteer> volunteers = new HashSet<>();

    public Action() {
        this.descr = new ArrayList<>();
        this.begin = Actions.noDate;
        this.end = Actions.noDate;
    }
}
