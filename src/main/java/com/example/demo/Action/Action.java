package com.example.demo.Action;

import com.example.demo.Volunteer.Volunteer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(
            name = "action_descriptions",
            joinColumns = @JoinColumn(name = "action_id"),
            inverseJoinColumns = @JoinColumn(name = "description_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "language")
    private HashMap<Lang, Description> descr;

    @ManyToMany(mappedBy = "actions", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Volunteer> volunteers = new HashSet<>();

    public Action() {
        this.descr = new HashMap<>();
        for (Lang lang : Lang.values()) {
            this.descr.put(lang, new Description());
        }
        this.begin = Actions.noDate;
        this.end = Actions.noDate;
    }
}
