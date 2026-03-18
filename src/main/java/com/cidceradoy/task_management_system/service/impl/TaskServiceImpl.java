package com.cidceradoy.task_management_system.service.impl;

import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import com.cidceradoy.task_management_system.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<TaskView> getTasks(Task.Status status) {
        if (Objects.isNull(status)) {
            return taskRepository.getTasks()
                    .stream()
                    .map(taskViewInterface -> new TaskView(taskViewInterface.getId(),
                            taskViewInterface.getTitle(), taskViewInterface.getDescription(),
                            taskViewInterface.getStatus(), taskViewInterface.getDueDate()))
                    .toList();
        }
        return taskRepository.getTasksByStatus(status)
                .stream()
                .map(taskViewInterface -> new TaskView(taskViewInterface.getId(),
                        taskViewInterface.getTitle(), taskViewInterface.getDescription(),
                        taskViewInterface.getStatus(), taskViewInterface.getDueDate()))
                .toList();
    }
}