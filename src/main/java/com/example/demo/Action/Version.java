package com.example.demo.Action;

import com.example.demo.Model.ID;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "versions")
public class Version {
    @EmbeddedId
    private ID versionId;

    @Column(name = "valid", nullable = false)
    private boolean valid;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "place", nullable = false)
    private String place;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "hours", nullable = false)
    private String hours;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private ArrayList<Role> roles;

    public Version() {
        valid = false;
        roles = new ArrayList<>();
    }
}
