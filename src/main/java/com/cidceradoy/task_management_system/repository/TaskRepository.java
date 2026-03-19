package com.cidceradoy.task_management_system.repository;

import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t.id AS id, " +
            "   t.title AS title, " +
            "   t.description AS description, " +
            "   t.status AS status, " +
            "   t.dueDate as dueDate " +
            "FROM Task t ")
    List<TaskView.TaskViewInterface> getTasks();

    @Query("SELECT t.id AS id, " +
            "   t.title AS title, " +
            "   t.description AS description, " +
            "   t.status AS status, " +
            "   t.dueDate as dueDate " +
            "FROM Task t " +
            "WHERE t.status = :status")
    List<TaskView.TaskViewInterface> getTasksByStatus(@Param(value = "status") Task.Status status);

    @Query("SELECT t " +
            "FROM Task t " +
            "WHERE t.title = :title")
    Optional<Task> findByTitle(String title);
}
