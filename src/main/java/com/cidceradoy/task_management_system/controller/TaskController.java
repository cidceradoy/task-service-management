package com.cidceradoy.task_management_system.controller;

import com.cidceradoy.task_management_system.dto.TaskForm;
import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskView>> getTasks(@RequestParam(value = "status", required = false) Task.Status status) {
        List<TaskView> tasks = taskService.getTasks(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskView> getTaskById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    public ResponseEntity<String> createTask(@Valid @RequestBody TaskForm form, UriComponentsBuilder uriBuilder) {
        UUID createdId = taskService.createTask(form);

        URI uri = uriBuilder
                .path("/tasks/{id}")
                .buildAndExpand(createdId)
                .toUri();

        return ResponseEntity.created(uri).body("Task with id: " + createdId + " created.");
    }
}
