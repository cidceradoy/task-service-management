package com.cidceradoy.task_management_system.dto;

import com.cidceradoy.task_management_system.validation.ValidStatus;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public class TaskUpdateForm {

    private String title;

    private String description;

    @ValidStatus
    private String status;

    @Future(message = "Due date cannot be current or past timestamp")
    private LocalDateTime dueDate;

    public TaskUpdateForm(String title, String description, String status, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }
}
