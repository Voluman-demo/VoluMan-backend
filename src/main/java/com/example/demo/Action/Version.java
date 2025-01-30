package com.example.demo.Action;

import jakarta.persistence.*;
import lombok.*;

@Data
@MappedSuperclass
public class Version {
    @Column(name = "valid")
    private boolean valid;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "place")
    private String place;

    @Column(name = "address")
    private String address;

    @Column(name = "description")
    private String description;

    @Column(name = "hours")
    private String hours;


    public Version() {
        valid = false;
    }
}
