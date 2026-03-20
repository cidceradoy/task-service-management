package com.cidceradoy.task_management_system.service;

import com.cidceradoy.task_management_system.dto.TaskForm;
import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    Page<TaskView> getTasks(Task.Status status, Pageable pageable);
    TaskView getTaskById(UUID id);
    UUID createTask(TaskForm form);
    UUID updateTask(UUID id, TaskForm form);
    void deleteTask(UUID id);
}
