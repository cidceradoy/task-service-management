package com.cidceradoy.task_management_system.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String title;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    private LocalDateTime dueDate;

    public Task(String title, String description, Status status, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    protected Task() {
        // required by JPA
    }

    public enum Status {
        PENDING, IN_PROGRESS, DONE
    }
}
