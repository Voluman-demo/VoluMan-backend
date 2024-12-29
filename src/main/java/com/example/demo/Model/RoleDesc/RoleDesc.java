package com.example.demo.Model.RoleDesc;


import com.example.demo.Action.SingleAction;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_desc")
public class RoleDesc{
    @Id
    private Long id;
    String title;
    String description;

    @JsonBackReference
    @ManyToMany(mappedBy = "roles")
    private ArrayList<SingleAction> actions = new ArrayList<>();
}
