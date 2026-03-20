package com.cidceradoy.task_management_system.service;

import com.cidceradoy.task_management_system.dto.TaskForm;
import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.exception.InvalidStatusException;
import com.cidceradoy.task_management_system.exception.ResourceNotFoundException;
import com.cidceradoy.task_management_system.exception.TitleAlreadyExistsException;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import com.cidceradoy.task_management_system.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Captor
    ArgumentCaptor<Task.Status> statusCaptor;

    @Captor
    ArgumentCaptor<Task> taskCaptor;

    @Test
    public void getTasks_statusIsNullAndEmptyTasks_returnEmptyList() {
        Pageable pageable = mock(Pageable.class);
        when(taskRepository.getTasks(pageable)).thenReturn(Page.empty());

        Page<TaskView> result = taskService.getTasks(null, pageable);

        verify(taskRepository, times(1)).getTasks(pageable);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(0);
    }

    @Test
    public void getTasks_statusIsNull_returnAllTasks() {
        Pageable pageable = mock(Pageable.class);
        TaskView.TaskViewInterface task1 = mock(TaskView.TaskViewInterface.class);
        TaskView.TaskViewInterface task2 = mock(TaskView.TaskViewInterface.class);
        TaskView.TaskViewInterface task3 = mock(TaskView.TaskViewInterface.class);

        when(task1.getStatus()).thenReturn("PENDING");
        when(task2.getStatus()).thenReturn("IN_PROGRESS");
        when(task3.getStatus()).thenReturn("DONE");

        when(taskRepository.getTasks(pageable)).thenReturn(
          new PageImpl<>(List.of(task1, task2, task3))
        );

        Page<TaskView> result = taskService.getTasks(null, pageable);

        verify(taskRepository, times(1)).getTasks(pageable);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).extracting(TaskView::getStatus)
                .containsExactlyInAnyOrder("PENDING", "IN_PROGRESS", "DONE");
    }

    @Test
    public void getTasks_statusIsPending_returnPendingTasks() {
        Pageable pageable = mock(Pageable.class);
        TaskView.TaskViewInterface task = mock(TaskView.TaskViewInterface.class);
        when(task.getStatus()).thenReturn("PENDING");

        when(taskRepository.getTasksByStatus(Task.Status.PENDING, pageable)).thenReturn(
                new PageImpl<>(
                        List.of(task)
                )
        );

        Page<TaskView> result = taskService.getTasks(Task.Status.PENDING, pageable);

        verify(taskRepository, times(1)).getTasksByStatus(statusCaptor.capture(), ArgumentMatchers.eq(pageable));
        assertThat(statusCaptor.getValue()).isEqualTo(Task.Status.PENDING);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).extracting(TaskView::getStatus)
                .containsExactly("PENDING");
    }

    @Test
    public void getTasks_statusIsInProgress_returnInProgressTasks() {
        Pageable pageable = mock(Pageable.class);
        TaskView.TaskViewInterface task = mock(TaskView.TaskViewInterface.class);
        when(task.getStatus()).thenReturn("IN_PROGRESS");

        when(taskRepository.getTasksByStatus(Task.Status.IN_PROGRESS, pageable)).thenReturn(
                new PageImpl<>(
                        List.of(task)
                )
        );

        Page<TaskView> result = taskService.getTasks(Task.Status.IN_PROGRESS, pageable);

        verify(taskRepository, times(1)).getTasksByStatus(statusCaptor.capture(), ArgumentMatchers.eq(pageable));
        assertThat(statusCaptor.getValue()).isEqualTo(Task.Status.IN_PROGRESS);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).extracting(TaskView::getStatus)
                .containsExactly("IN_PROGRESS");
    }

    @Test
    public void getTasks_statusIsDone_returnDoneTasks() {
        Pageable pageable = mock(Pageable.class);
        TaskView.TaskViewInterface task = mock(TaskView.TaskViewInterface.class);
        when(task.getStatus()).thenReturn("DONE");

        when(taskRepository.getTasksByStatus(Task.Status.DONE, pageable)).thenReturn(
                new PageImpl<>(
                        List.of(task)
                )
        );

        Page<TaskView> result = taskService.getTasks(Task.Status.DONE, pageable);

        verify(taskRepository, times(1)).getTasksByStatus(statusCaptor.capture(), ArgumentMatchers.eq(pageable));
        assertThat(statusCaptor.getValue()).isEqualTo(Task.Status.DONE);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).extracting(TaskView::getStatus)
                .containsExactly("DONE");
    }

    @Test
    public void getTaskById_taskFound_returnTask() {
        UUID id = UUID.randomUUID();
        Task task = new Task("title-1", "desc-1", Task.Status.PENDING, LocalDateTime.now());
        ReflectionTestUtils.setField(task, "id", id);
        when(taskRepository.findById(any())).thenReturn(Optional.of(task));


        TaskView result = taskService.getTaskById(id);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(task.getTitle());
        assertThat(result.getDescription()).isEqualTo(task.getDescription());
        assertThat(result.getStatus()).isEqualTo(task.getStatus().name());
        assertThat(result.getDueDate()).isEqualTo(task.getDueDate());
    }

    @Test
    public void getTaskById_taskNotFound_throwResourceNotFoundException() {
        UUID id = UUID.randomUUID();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.getTaskById(id));

        assertThat(exception.getMessage()).isEqualTo("Task with id " + id + " not found.");
    }

    @Test
    public void createTask_validForm_returnIdOfNewTask() {
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));

        Task task = new Task("t-1", "d-1", Task.Status.PENDING, LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(task, "id", UUID.randomUUID());

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        UUID id = taskService.createTask(form);

        verify(taskRepository, times(1)).save(any(Task.class));
        assertThat(id).isEqualTo(task.getId());
    }

    @Test
    public void createTask_dueDatePastAndStatusPending_throwInvalidStatusException() {
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().minusDays(1));

        InvalidStatusException exception = assertThrows(InvalidStatusException.class,
                () -> taskService.createTask(form));

        assertThat(exception.getMessage()).isEqualTo("PENDING status cannot be set when due date is in the past");
    }

    @Test
    public void createTask_titleAlreadyExists_throwTitleAlreadyExistsException() {
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));

        when(taskRepository.findByTitle(any(String.class))).thenReturn(Optional.of(mock(Task.class)));

        TitleAlreadyExistsException exception = assertThrows(TitleAlreadyExistsException.class,
                () -> taskService.createTask(form));

        assertThat(exception.getMessage()).isEqualTo("Task with title: " + form.getTitle() + " already exists.");
    }

    @Test
    public void updateTask_validFormAndId_updateTask() {
        UUID id = UUID.randomUUID();
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));

        Task task = new Task("t-1", "d-1", Task.Status.PENDING, LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(task, "id", id);

        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.of(task));
        when(taskRepository.findByTitle(task.getTitle())).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        UUID result = taskService.updateTask(id, form);

        verify(taskRepository, times(1)).save(taskCaptor.capture());
        assertThat(result).isEqualTo(taskCaptor.getValue().getId());
    }

    @Test
    public void updateTask_nonExistingTask_throwResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        TaskForm form = mock(TaskForm.class);

        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(id, form));

        assertThat(exception.getMessage()).isEqualTo("Task with id " + id + " not found.");
    }

    @Test
    public void updateTask_titleInvalid_throwTitleAlreadyExistsException() {
        UUID id = UUID.randomUUID();
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));

        Task task = new Task("t-1", "d-1", Task.Status.PENDING, LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(task, "id", id);

        UUID id2 = UUID.randomUUID();
        Task task2 = new Task("t-1", "d-1", Task.Status.PENDING, LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(task, "id", id2);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.findByTitle("t-1")).thenReturn(Optional.of(task2));

        TitleAlreadyExistsException exception = assertThrows(TitleAlreadyExistsException.class,
                () -> taskService.updateTask(id, form));

        assertThat(exception.getMessage()).isEqualTo("Task with title: " + form.getTitle() + " already exists.");
    }

    @Test
    public void updateTask_dueDatePastAndStatusPending_throwInvalidStatusException() {
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().minusDays(1));
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(id)).thenReturn(Optional.of(mock(Task.class)));

        InvalidStatusException exception = assertThrows(InvalidStatusException.class,
                () -> taskService.updateTask(id, form));

        assertThat(exception.getMessage()).isEqualTo("PENDING status cannot be set when due date is in the past");
    }

    @Test
    public void deleteTask_taskExists_taskDeleted() {
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(id)).thenReturn(Optional.of(mock(Task.class)));

        taskService.deleteTask(id);

        verify(taskRepository, times(1)).deleteById(id);
    }

    @Test
    public void deleteTask_taskNotExists_throwResourceNotFoundException() {
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(id));

        assertThat(exception.getMessage()).isEqualTo("Task with id " + id + " not found.");
    }
}
