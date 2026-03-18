package com.cidceradoy.task_management_system.controller;

import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import com.cidceradoy.task_management_system.service.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
}