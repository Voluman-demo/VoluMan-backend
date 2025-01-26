package com.example.demo.Log;

import com.example.demo.Model.ID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "log")
public class Log {
    @EmbeddedId
    private ID id;

    private String firstName;
    private String lastName;
    private String email;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String eventDesc;

    private LocalDateTime timestamp;
}
