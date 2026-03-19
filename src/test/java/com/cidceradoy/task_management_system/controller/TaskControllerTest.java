package com.cidceradoy.task_management_system.controller;

import com.cidceradoy.task_management_system.dto.TaskCreateForm;
import com.cidceradoy.task_management_system.dto.TaskUpdateForm;
import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.exception.ResourceNotFoundException;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import com.cidceradoy.task_management_system.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

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

    @Test
    public void createTask_validRequestBody_createNewTask() throws Exception {
        UUID id = UUID.randomUUID();
        TaskCreateForm form = new TaskCreateForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));
        when(taskService.createTask(ArgumentMatchers.any(TaskCreateForm.class))).thenReturn(id);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Task with id: " + id + " created."));
    }

    @Test
    public void createTask_existingTitle_throwMethodArgumentNotValidException() throws Exception {
        TaskCreateForm form = new TaskCreateForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));
        Task existingTask = mock(Task.class);
        when(taskRepository.findByTitle(ArgumentMatchers.any(String.class))).thenReturn(Optional.of(existingTask));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Title should be unique")));
    }

    @Test
    public void createTask_pastDueDate_throwMethodArgumentNotValidException() throws Exception {
        TaskCreateForm form = new TaskCreateForm("t-1", "d-1", "PENDING", LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.dueDate", is("Due date cannot be current or past timestamp")));
    }

    @Test
    public void createTask_invalidStatusType_throwHttpMessageNotReadableException() throws Exception {
        TaskCreateForm form = new TaskCreateForm("t-1", "d-1", "notvalidstatus", LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("Status must be one of values: [DONE, IN_PROGRESS, PENDING]")));
    }

    @Test
    public void updateTask_validRequestBodyAndId_updateTask() throws Exception {
        TaskUpdateForm form = new TaskUpdateForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));
        UUID id = UUID.randomUUID();

        when(taskService.updateTask(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(TaskUpdateForm.class))).thenReturn(id);

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task with id: " + id + " updated."));
    }

    @Test
    public void updateTask_taskDoNotExists_throwResourceNotFoundException() throws Exception {
        UUID id = UUID.randomUUID();
        TaskCreateForm form = new TaskCreateForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));

        when(taskService.updateTask(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(TaskUpdateForm.class)))
                .thenThrow(new ResourceNotFoundException("Task with id " + id + " not found."));

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task with id " + id + " not found.")));
    }

    @Test
    public void updateTask_statusNotValid_throwMethodArgumentTypeMismatchException() throws Exception {
        UUID id = UUID.randomUUID();
        TaskCreateForm form = new TaskCreateForm("t-1", "d-1", "notvalid", LocalDateTime.now().plusDays(1));

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("Status must be one of values: [DONE, IN_PROGRESS, PENDING]")));
    }

    @Test
    public void updateTask_pastDueDate_throwMethodArgumentTypeMismatchException() throws Exception {
        UUID id = UUID.randomUUID();
        TaskCreateForm form = new TaskCreateForm("t-1", "d-1", "PENDING", LocalDateTime.now().minusDays(1));


        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.dueDate", is("Due date cannot be current or past timestamp")));
    }

    @Test
    public void deleteTask_existingTask_taskDeleted() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(taskService).deleteTask(id);

        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string("Task with id: " + id + " deleted."));

        verify(taskService, times(1)).deleteTask(id);
    }

    @Test
    public void deleteTask_nonExistingTask_throwResourceNotFoundException() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Task with id " + id + " not found."))
                .when(taskService).deleteTask(id);

        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task with id " + id + " not found.")));
    }
}