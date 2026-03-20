package com.cidceradoy.task_management_system.dto;

import com.cidceradoy.task_management_system.validation.ValidStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TaskForm {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @ValidStatus
    @NotBlank(message = "Status cannot be blank")
    private String status;

//    Ideally dueDate should not be past timestamp
//    @Future(message = "Due date cannot be current or past timestamp")
    @NotNull(message = "Due date cannot be null")
    private LocalDateTime dueDate;

    public TaskForm(String title, String description, String status, LocalDateTime dueDate) {
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
