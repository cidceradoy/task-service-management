package com.cidceradoy.task_management_system.dto;

import java.time.LocalDateTime;

public class TaskView {
    private String id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime dueDate;

    public TaskView(String id, String title, String description, String status, LocalDateTime dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
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

    public interface TaskViewInterface {
        String getId();
        String getTitle();
        String getDescription();
        String getStatus();
        LocalDateTime getDueDate();
    }
}
