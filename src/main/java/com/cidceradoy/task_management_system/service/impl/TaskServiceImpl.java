package com.cidceradoy.task_management_system.service.impl;

import com.cidceradoy.task_management_system.dto.TaskForm;
import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.exception.InvalidStatusException;
import com.cidceradoy.task_management_system.exception.ResourceNotFoundException;
import com.cidceradoy.task_management_system.exception.TitleAlreadyExistsException;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import com.cidceradoy.task_management_system.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public TaskView getTaskById(UUID id) {
        return taskRepository.findById(id)
                .map(t -> new TaskView(t.getId().toString(), t.getTitle(), t.getDescription(),
                        t.getStatus().name(), t.getDueDate()))
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found."));
    }

    @Override
    @Transactional
    public UUID createTask(TaskForm form) {
        if (form.getDueDate().isBefore(LocalDateTime.now()) && form.getStatus().equals("PENDING")) {
            throw new InvalidStatusException("PENDING status cannot be set when due date is in the past");
        }

        Optional<Task> task = taskRepository.findByTitle(form.getTitle());
        if (task.isPresent()) {
            throw new TitleAlreadyExistsException("Task with title: " + form.getTitle() + " already exists.");
        }

        Task newTask = new Task(form.getTitle(), form.getDescription(), Task.Status.valueOf(form.getStatus()), form.getDueDate());
        Task createdTask = taskRepository.save(newTask);
        return createdTask.getId();
    }

    @Override
    @Transactional
    public UUID updateTask(UUID id, TaskForm form) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            throw new ResourceNotFoundException("Task with id " + id + " not found.");
        }

        if (Objects.nonNull(form.getDueDate()) && Objects.nonNull(form.getStatus()) &&
                form.getDueDate().isBefore(LocalDateTime.now()) && form.getStatus().equals("PENDING")) {
            throw new InvalidStatusException("PENDING status cannot be set when due date is in the past");
        }

        if (!isTitleUnique(task.get(), form.getTitle())) {
            throw new TitleAlreadyExistsException("Task with title: " + form.getTitle() + " already exists.");
        }

        task.get().setTitle(form.getTitle());
        task.get().setDescription(form.getDescription());
        task.get().setStatus(Task.Status.valueOf(form.getStatus()));
        task.get().setDueDate(form.getDueDate());

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

    private boolean isTitleUnique(Task task, String title) {
        Optional<Task> t = taskRepository.findByTitle(title);
        return t.isEmpty() || Objects.equals(t.get().getId(), task.getId());
    }
}