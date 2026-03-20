package com.cidceradoy.task_management_system.controller;

import com.cidceradoy.task_management_system.dto.SuccessView;
import com.cidceradoy.task_management_system.dto.TaskForm;
import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<SuccessView> createTask(@Valid @RequestBody TaskForm form, UriComponentsBuilder uriBuilder) {
        UUID createdId = taskService.createTask(form);

        URI uri = uriBuilder
                .path("/api/tasks/{id}")
                .buildAndExpand(createdId)
                .toUri();

        return ResponseEntity.created(uri).body(new SuccessView("Task with id: " + createdId + " created."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessView> updateTask(@PathVariable(value = "id") UUID id, @Valid @RequestBody TaskForm form,
                                             UriComponentsBuilder uriBuilder) {
        UUID updatedId = taskService.updateTask(id, form);

        URI uri = uriBuilder
                .path("/api/tasks/{id}")
                .buildAndExpand(updatedId)
                .toUri();

        return ResponseEntity.ok()
                .header("Location", uri.toString())
                .body(new SuccessView("Task with id: " + updatedId + " updated."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessView> deleteTask(@PathVariable(value = "id") UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok()
                .body(new SuccessView("Task with id: " + id + " deleted."));
    }
}
