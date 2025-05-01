package org.example.to_dolist.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
public class Task {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String description;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
