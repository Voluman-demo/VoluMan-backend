package com.example.demo.Action;

import com.example.demo.Model.ID;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "versions")
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private ID versionId;

    @Column(name = "valid", nullable = false)
    private boolean valid;

    @Column(name = "full_name", nullable = false)
    private String fullName; // Full name of the action

    @Column(name = "short_name", nullable = false)
    private String shortName; // Abbreviation or acronym

    @Column(name = "place", nullable = false)
    private String place; // Name of the place

    @Column(name = "address", nullable = false)
    private String address; // Address of the action

    @Column(name = "description", nullable = false)
    private String description; // Description of the action

    @Column(name = "hours", nullable = false)
    private String hours; // Working hours

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private ArrayList<Role> roles;

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean v) {
        this.valid = v;
    }

    public Version(){
        valid = false;
        roles = new ArrayList<>();
    }
}
