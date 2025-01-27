package com.example.demo.Action;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "descriptions")
public class Description  extends Version {
//    @EmbeddedId
//    private ID descriptionId;

    @Column(name = "start_date")
    private LocalDate begin;

    @Column(name = "end_date")
    private LocalDate end;



}
