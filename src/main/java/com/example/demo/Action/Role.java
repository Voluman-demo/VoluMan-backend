package com.example.demo.Action;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "action_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    private String name;
    private String duties;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "description_id", nullable = false)
    @JsonBackReference
    private Description description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId) &&
                Objects.equals(name, role.name) &&
                Objects.equals(duties, role.duties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, name, duties);
    }
}
