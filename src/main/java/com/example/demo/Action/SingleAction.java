package com.example.demo.Action;

import com.example.demo.Model.Language.Language;
import com.example.demo.Model.RoleDesc.RoleDesc;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "single_action")
public class SingleAction {
    @Id
    @Column(name = "signle_action_id")
    private Long singleActionId;

    private String shortName;
    private String fullName;
    private String place;
    private String description;
    private LocalDate actionBeg;
    private LocalDate actionEnd;

    @ManyToMany
    @JoinTable(
            name = "action_roles",
            joinColumns = @JoinColumn(name = "action_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private ArrayList<RoleDesc> roles = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "action_languages",
            joinColumns = @JoinColumn(name = "action_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private ArrayList<Language> languages = new ArrayList<>();
}