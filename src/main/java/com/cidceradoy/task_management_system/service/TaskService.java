package com.cidceradoy.task_management_system.service;

import com.cidceradoy.task_management_system.dto.TaskForm;
import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskView> getTasks(Task.Status status);
    TaskView getTaskById(UUID id);
    UUID createTask(TaskForm form);
    UUID updateTask(UUID id, TaskForm form);
    void deleteTask(UUID id);
}
