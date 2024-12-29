package com.example.demo.Model.Language;

import com.example.demo.Action.SingleAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "language")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private LangISO type;

    @ManyToMany(mappedBy = "languages")
    private ArrayList<SingleAction> actions = new ArrayList<>();
}
