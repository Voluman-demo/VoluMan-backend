package com.example.demo.Action;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "descriptions")
public class Description extends Version{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="description_id")
    private Long descriptionId;

    @Column(name = "start_date")
    private LocalDate begin;

    @Column(name = "end_date")
    private LocalDate end;

    @Enumerated(EnumType.STRING)
    @Column(name = "lang", unique = true)
    private Lang lang;

    @OneToMany(mappedBy = "description", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Role> roles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;
}
