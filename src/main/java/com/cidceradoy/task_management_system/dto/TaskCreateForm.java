package com.cidceradoy.task_management_system.dto;

import com.cidceradoy.task_management_system.validation.UniqueTitle;
import com.cidceradoy.task_management_system.validation.ValidStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TaskCreateForm {

    @UniqueTitle
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @ValidStatus
    @NotBlank
    private String status;

    @Future(message = "Due date cannot be current or past timestamp")
    @NotNull
    private LocalDateTime dueDate;

    public TaskCreateForm(String title, String description, String status, LocalDateTime dueDate) {
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
