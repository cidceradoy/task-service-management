package com.cidceradoy.task_management_system.service;

import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.model.Task;

import java.util.List;

public interface TaskService {
    List<TaskView> getTasks(Task.Status status);
}
