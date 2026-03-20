package com.cidceradoy.task_management_system.repository;

import com.cidceradoy.task_management_system.dto.TaskView;
import com.cidceradoy.task_management_system.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    Page<TaskView.TaskViewInterface> getTasks(Pageable pageable);

    @Query("SELECT t.id AS id, " +
            "   t.title AS title, " +
            "   t.description AS description, " +
            "   t.status AS status, " +
            "   t.dueDate as dueDate " +
            "FROM Task t " +
            "WHERE t.status = :status")
    Page<TaskView.TaskViewInterface> getTasksByStatus(@Param(value = "status") Task.Status status, Pageable pageable);

    @Query("SELECT t " +
            "FROM Task t " +
            "WHERE t.title = :title")
    Optional<Task> findByTitle(String title);
}
