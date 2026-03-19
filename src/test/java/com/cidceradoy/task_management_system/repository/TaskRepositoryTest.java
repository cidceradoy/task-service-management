package com.cidceradoy.task_management_system.repository;

import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    public void setup() {
        taskRepository.saveAll(
            List.of(
                new Task("title-1", "desc-1", Task.Status.PENDING, LocalDateTime.now().plusDays(2)),
                new Task("title-2", "desc-2", Task.Status.PENDING, LocalDateTime.now().plusDays(1)),
                new Task("title-3", "desc-3", Task.Status.IN_PROGRESS, LocalDateTime.now()),
                new Task("title-4", "desc-4", Task.Status.DONE, LocalDateTime.now().minusDays(1))
            )
        );
    }

    @AfterEach
    public void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    public void getTasks_returnAllTasksByStatus() {
        List<TaskView.TaskViewInterface> result = taskRepository.getTasks();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(4);
        assertThat(result).extracting(TaskView.TaskViewInterface::getTitle)
                .containsExactlyInAnyOrder("title-1", "title-2", "title-3", "title-4");
    }

    @Test
    public void getTasksByStatus_statusPending_returnAllPendingTasks() {
        List<TaskView.TaskViewInterface> result = taskRepository.getTasksByStatus(Task.Status.PENDING);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(TaskView.TaskViewInterface::getTitle)
                .containsExactlyInAnyOrder("title-1", "title-2");
    }

    @Test
    public void getTasksByStatus_statusInProgress_returnAllInProgressTasks() {
        List<TaskView.TaskViewInterface> result = taskRepository.getTasksByStatus(Task.Status.IN_PROGRESS);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).extracting(TaskView.TaskViewInterface::getTitle)
                .containsExactly("title-3");
    }

    @Test
    public void getTasksByStatus_statusDone_returnAllDoneTasks() {
        List<TaskView.TaskViewInterface> result = taskRepository.getTasksByStatus(Task.Status.DONE);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).extracting(TaskView.TaskViewInterface::getTitle)
                .containsExactly("title-4");
    }

    @Test
    public void findByTitle_existingTitle_returnTaskWithTheTitle() {
        Optional<Task> task = taskRepository.findByTitle("title-1");

        assertThat(task.isPresent()).isTrue();
        assertThat(task.get()).extracting(Task::getTitle)
                .isEqualTo("title-1");
    }
}
