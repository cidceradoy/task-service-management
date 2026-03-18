package com.cidceradoy.task_management_system.controller;

import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.exception.ResourceNotFoundException;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    public void getTasks_databaseEmpty_returnEmptyTasks() throws Exception {
        when(taskService.getTasks(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getTasks_noFilter_returnAllTasks() throws Exception {
        when(taskService.getTasks(null)).thenReturn(
            List.of(
                new TaskView("id-1", "title-1", "desc-1", "PENDING", LocalDateTime.now().plusDays(2)),
                new TaskView("id-2", "title-2", "desc-2", "PENDING", LocalDateTime.now().plusDays(1)),
                new TaskView("id-3", "title-3", "desc-3", "IN_PROGRESS", LocalDateTime.now().plusDays(1)),
                new TaskView("id-4", "title-4", "desc-4", "DONE", LocalDateTime.now().minusDays(1))
            )
        );

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void getTasks_filterByPending_returnPendingTasks() throws Exception {
        when(taskService.getTasks(Task.Status.PENDING)).thenReturn(
            List.of(
                new TaskView("id-1", "title-1", "desc-1", "PENDING", LocalDateTime.now().plusDays(2)),
                new TaskView("id-2", "title-2", "desc-2", "PENDING", LocalDateTime.now().plusDays(1))
            )
        );
        mockMvc.perform(get("/api/tasks?status=PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[*].status", everyItem(is("PENDING"))));
    }

    @Test
    public void getTasks_filterByInProgress_returnInProgressTasks() throws Exception {
        when(taskService.getTasks(Task.Status.IN_PROGRESS)).thenReturn(
            List.of(new TaskView("id-3", "title-3", "desc-3", "IN_PROGRESS", LocalDateTime.now().plusDays(1)))
        );

        mockMvc.perform(get("/api/tasks?status=IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[*].status", everyItem(is("IN_PROGRESS"))));
    }

    @Test
    public void getTasks_filterByDone_returnDoneTasks() throws Exception {
        when(taskService.getTasks(Task.Status.DONE)).thenReturn(
            List.of(new TaskView("id-4", "title-4", "desc-4", "DONE", LocalDateTime.now().minusDays(1)))
        );

        mockMvc.perform(get("/api/tasks?status=DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[*].status", everyItem(is("DONE"))));
    }

    @Test
    public void getTaskById_taskExists_returnTask() throws Exception {
        UUID id = UUID.randomUUID();
        TaskView task = new TaskView(id.toString(), "title-1", "desc-1", "PENDING", LocalDateTime.now().plusDays(2));
        when(taskService.getTaskById(ArgumentMatchers.any(UUID.class))).thenReturn(task);

        mockMvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())));
    }

    @Test
    public void getTaskById_taskDoNotExist_throwResourceNotFoundException() throws Exception {
        UUID id = UUID.randomUUID();
        when(taskService.getTaskById(ArgumentMatchers.any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("Task with id " + id + " not found."));

        mockMvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task with id " + id + " not found.")));
    }

    @Test
    public void getTaskById_invalidId_throwMethodArgumentTypeMismatchException() throws Exception {
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid UUID: 1")));
    }
}