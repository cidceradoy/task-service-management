package com.cidceradoy.task_management_system.service;

import com.cidceradoy.task_management_system.dto.TaskForm;
import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.exception.ResourceNotFoundException;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import com.cidceradoy.task_management_system.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        when(taskRepository.getTasks()).thenReturn(Collections.emptyList());

        List<TaskView> result = taskService.getTasks(null);

        verify(taskRepository, times(1)).getTasks();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(0);
    }

    @Test
    public void getTasks_statusIsNull_returnAllTasks() {
        TaskView.TaskViewInterface task1 = mock(TaskView.TaskViewInterface.class);
        TaskView.TaskViewInterface task2 = mock(TaskView.TaskViewInterface.class);
        TaskView.TaskViewInterface task3 = mock(TaskView.TaskViewInterface.class);

        when(task1.getStatus()).thenReturn("PENDING");
        when(task2.getStatus()).thenReturn("IN_PROGRESS");
        when(task3.getStatus()).thenReturn("DONE");

        when(taskRepository.getTasks()).thenReturn(
          List.of(task1, task2, task3)
        );

        List<TaskView> result = taskService.getTasks(null);

        verify(taskRepository, times(1)).getTasks();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).extracting(TaskView::getStatus)
                .containsExactlyInAnyOrder("PENDING", "IN_PROGRESS", "DONE");
    }

    @Test
    public void getTasks_statusIsPending_returnPendingTasks() {
        TaskView.TaskViewInterface task = mock(TaskView.TaskViewInterface.class);
        when(task.getStatus()).thenReturn("PENDING");

        when(taskRepository.getTasksByStatus(Task.Status.PENDING)).thenReturn(
                List.of(task)
        );

        List<TaskView> result = taskService.getTasks(Task.Status.PENDING);

        verify(taskRepository, times(1)).getTasksByStatus(statusCaptor.capture());
        assertThat(statusCaptor.getValue()).isEqualTo(Task.Status.PENDING);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).extracting(TaskView::getStatus)
                .containsExactly("PENDING");
    }

    @Test
    public void getTasks_statusIsInProgress_returnInProgressTasks() {
        TaskView.TaskViewInterface task = mock(TaskView.TaskViewInterface.class);
        when(task.getStatus()).thenReturn("IN_PROGRESS");

        when(taskRepository.getTasksByStatus(Task.Status.IN_PROGRESS)).thenReturn(
            List.of(task)
        );

        List<TaskView> result = taskService.getTasks(Task.Status.IN_PROGRESS);

        verify(taskRepository, times(1)).getTasksByStatus(statusCaptor.capture());
        assertThat(statusCaptor.getValue()).isEqualTo(Task.Status.IN_PROGRESS);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).extracting(TaskView::getStatus)
                .containsExactly("IN_PROGRESS");
    }

    @Test
    public void getTasks_statusIsDone_returnDoneTasks() {
        TaskView.TaskViewInterface task = mock(TaskView.TaskViewInterface.class);
        when(task.getStatus()).thenReturn("DONE");

        when(taskRepository.getTasksByStatus(Task.Status.DONE)).thenReturn(
                List.of(task)
        );

        List<TaskView> result = taskService.getTasks(Task.Status.DONE);

        verify(taskRepository, times(1)).getTasksByStatus(statusCaptor.capture());
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
    public void createTask_returnIdOfNewTask() {
        TaskForm form = new TaskForm("t-1", "d-1", "PENDING", LocalDateTime.now().plusDays(1));

        Task task = new Task("t-1", "d-1", Task.Status.PENDING, LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(task, "id", UUID.randomUUID());

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        UUID id = taskService.createTask(form);

        verify(taskRepository, times(1)).save(any(Task.class));
        assertThat(id).isEqualTo(task.getId());
    }
}
