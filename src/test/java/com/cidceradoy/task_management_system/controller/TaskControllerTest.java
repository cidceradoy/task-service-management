package com.cidceradoy.task_management_system.controller;

import com.cidceradoy.task_management_system.dto.TaskForm;
import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.exception.ResourceNotFoundException;
import com.cidceradoy.task_management_system.exception.TitleAlreadyExistsException;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import com.cidceradoy.task_management_system.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @MockitoBean
    private TaskRepository taskRepository;

    @Test
    public void getTasks_databaseEmpty_returnEmptyTasks() throws Exception {
        when(taskService.getTasks(eq(null), ArgumentMatchers.any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    public void getTasks_noFilter_returnAllTasks() throws Exception {
        when(taskService.getTasks(eq(null), ArgumentMatchers.any(Pageable.class))).thenReturn(
                new PageImpl<>(List.of(
                        new TaskView("id-1", "title-1", "desc-1", "PENDING", LocalDateTime.now().plusDays(2)),
                        new TaskView("id-2", "title-2", "desc-2", "PENDING", LocalDateTime.now().plusDays(1)),
                        new TaskView("id-3", "title-3", "desc-3", "IN_PROGRESS", LocalDateTime.now().plusDays(1)),
                        new TaskView("id-4", "title-4", "desc-4", "DONE", LocalDateTime.now().minusDays(1))
                ))
        );

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(4)));
    }

    @Test
    public void getTasks_filterByPending_returnPendingTasks() throws Exception {
        when(taskService.getTasks(ArgumentMatchers.eq(Task.Status.PENDING), ArgumentMatchers.any(Pageable.class))).thenReturn(
                new PageImpl<>(List.of(
                        new TaskView("id-1", "title-1", "desc-1", "PENDING", LocalDateTime.now().plusDays(2)),
                        new TaskView("id-2", "title-2", "desc-2", "PENDING", LocalDateTime.now().plusDays(1))
                ))
        );
        mockMvc.perform(get("/api/tasks")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("[*].status", everyItem(is("PENDING"))));
    }

    @Test
    public void getTasks_filterByInProgress_returnInProgressTasks() throws Exception {
        when(taskService.getTasks(ArgumentMatchers.eq(Task.Status.IN_PROGRESS), ArgumentMatchers.any(Pageable.class))).thenReturn(
                new PageImpl<>(
                        List.of(new TaskView("id-3", "title-3", "desc-3", "IN_PROGRESS", LocalDateTime.now().plusDays(1)))
                )
        );

        mockMvc.perform(get("/api/tasks")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[*].status", everyItem(is("IN_PROGRESS"))));
    }

    @Test
    public void getTasks_filterByDone_returnDoneTasks() throws Exception {
        when(taskService.getTasks(ArgumentMatchers.eq(Task.Status.DONE), ArgumentMatchers.any(Pageable.class))).thenReturn(
                new PageImpl<>(
                        List.of(new TaskView("id-4", "title-4", "desc-4", "DONE", LocalDateTime.now().minusDays(1)))
                )
        );

        mockMvc.perform(get("/api/tasks")
                        .param("status", "DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("[*].status", everyItem(is("DONE"))));
    }

    @Test
    public void getTasks_paginated_haveNumberZeroAndSizeOne() throws Exception {
        when(taskService.getTasks(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class))).thenReturn(
                new PageImpl<>(List.of(
                        new TaskView("id-4", "title-4", "desc-4", "DONE", LocalDateTime.now().minusDays(1))
                ))
        );

        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(1)));
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
                .andExpect(jsonPath("$.message", is("Invalid UUID string: 1")));
    }

    @Test
    public void createTask_validRequestBody_createNewTask() throws Exception {
        UUID id = UUID.randomUUID();
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));
        when(taskService.createTask(ArgumentMatchers.any(TaskForm.class))).thenReturn(id);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Task with id: " + id + " created.")));
    }

    @Test
    public void createTask_existingTitle_throwMethodArgumentNotValidException() throws Exception {
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));
        when(taskService.createTask(ArgumentMatchers.any(TaskForm.class))).thenThrow(new TitleAlreadyExistsException("Task with title: " + form.getTitle() + " already exists."));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Task with title: " + form.getTitle() + " already exists.")));
    }

    @Test
    public void createTask_titleIsBlank_throwMethodArgumentNotValidException() throws Exception {
        TaskForm form = new TaskForm("", "d-1", "PENDING", LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Title cannot be blank")));
    }

    @Test
    public void createTask_descriptionIsBlank_throwMethodArgumentNotValidException() throws Exception {
        TaskForm form = new TaskForm("t-1", "", "PENDING", LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", is("Description cannot be blank")));
    }

    @Test
    public void createTask_statusIsBlank_throwHttpMessageNotReadableException() throws Exception {
        TaskForm form = new TaskForm("t-1", "d-1", null, LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("Status cannot be blank")));
    }

    @Test
    public void createTask_invalidStatusType_throwHttpMessageNotReadableException() throws Exception {
        TaskForm form = new TaskForm("t-1", "d-1", "notvalidstatus", LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("Status must be one of values: [DONE, IN_PROGRESS, PENDING]")));
    }

    @Test
    public void createTask_dueDateIsNull_throwMethodArgumentNotValidException() throws Exception {
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", null);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.dueDate", is("Due date cannot be null")));
    }

    @Test
    public void updateTask_validRequestBodyAndId_updateTask() throws Exception {
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));
        UUID id = UUID.randomUUID();

        when(taskService.updateTask(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(TaskForm.class))).thenReturn(id);

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Task with id: " + id + " updated.")));
    }

    @Test
    public void updateTask_taskDoNotExists_throwResourceNotFoundException() throws Exception {
        UUID id = UUID.randomUUID();
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));

        when(taskService.updateTask(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(TaskForm.class)))
                .thenThrow(new ResourceNotFoundException("Task with id " + id + " not found."));

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task with id " + id + " not found.")));
    }

    @Test
    public void updateTask_titleIsBlank_throwMethodArgumentNotValidException() throws Exception {
        UUID id = UUID.randomUUID();
        TaskForm form = new TaskForm("", "d-1", "PENDING", LocalDateTime.now().plusDays(1));

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Title cannot be blank")));
    }

    @Test
    public void updateTask_descriptionIsBlank_throwMethodArgumentNotValidException() throws Exception {
        UUID id = UUID.randomUUID();
        TaskForm form = new TaskForm("t-1", "", "PENDING", LocalDateTime.now().plusDays(1));

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", is("Description cannot be blank")));
    }

    @Test
    public void updateTask_statusIsBlank_throwMethodArgumentNotValidException() throws Exception {
        UUID id = UUID.randomUUID();
        TaskForm form = new TaskForm("t-1", "d-1", null, LocalDateTime.now().plusDays(1));

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("Status cannot be blank")));
    }

    @Test
    public void updateTask_statusNotValid_throwMethodArgumentTypeMismatchException() throws Exception {
        UUID id = UUID.randomUUID();
        TaskForm form = new TaskForm("t-1", "d-1", "notvalid", LocalDateTime.now().plusDays(1));

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("Status must be one of values: [DONE, IN_PROGRESS, PENDING]")));
    }

    @Test
    public void updateTask_dueDateIsNull_throwMethodArgumentTypeMismatchException() throws Exception {
        UUID id = UUID.randomUUID();
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", null);

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.dueDate", is("Due date cannot be null")));
    }

    @Test
    public void deleteTask_existingTask_taskDeleted() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(taskService).deleteTask(id);

        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Task with id: " + id + " deleted.")));

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