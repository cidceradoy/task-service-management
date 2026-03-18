package com.cidceradoy.task_management_system.service.impl;

import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.exception.ResourceNotFoundException;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import com.cidceradoy.task_management_system.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public TaskView getTaskById(UUID id) {
        return taskRepository.findById(id)
                .map(t -> new TaskView(t.getId().toString(), t.getTitle(), t.getDescription(),
                        t.getStatus().name(), t.getDueDate()))
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found."));
    }
}