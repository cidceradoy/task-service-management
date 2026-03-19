package com.cidceradoy.task_management_system.service.impl;

import com.cidceradoy.task_management_system.dto.TaskCreateForm;
import com.cidceradoy.task_management_system.dto.TaskUpdateForm;
import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.exception.ResourceNotFoundException;
import com.cidceradoy.task_management_system.exception.TitleAlreadyExistsException;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import com.cidceradoy.task_management_system.service.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
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

    @Override
    @Transactional
    public UUID createTask(TaskCreateForm form) {
        Task newTask = new Task(form.getTitle(), form.getDescription(), Task.Status.valueOf(form.getStatus()), form.getDueDate());
        Task createdTask = taskRepository.save(newTask);
        return createdTask.getId();
    }

    @Override
    @Transactional
    public UUID updateTask(UUID id, TaskUpdateForm form) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            throw new ResourceNotFoundException("Task with id " + id + " not found.");
        }

        updateNonNullFields(task.get(), form);

        Task updatedTask = taskRepository.save(task.get());

        return updatedTask.getId();
    }

    @Override
    @Transactional
    public void deleteTask(UUID id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            throw new ResourceNotFoundException("Task with id " + id + " not found.");
        }

        taskRepository.deleteById(id);
    }

    private void updateNonNullFields(Task task, TaskUpdateForm form) {
        Optional.ofNullable(form.getTitle()).ifPresent(t -> {
            if (isValidTitleForUpdate(task, t)) {
                task.setTitle(t);
            } else {
                throw new TitleAlreadyExistsException("Title already exists");
            }
        });

        Optional.ofNullable(form.getDescription()).ifPresent(task::setDescription);
        Optional.ofNullable(form.getStatus()).map(Task.Status::valueOf).ifPresent(task::setStatus);
        Optional.ofNullable(form.getDueDate()).ifPresent(task::setDueDate);
    }

    private boolean isValidTitleForUpdate(Task task, String title) {
        Optional<Task> t = taskRepository.findByTitle(title);
        return t.isEmpty() || Objects.equals(t.get().getId(), task.getId());
    }
}