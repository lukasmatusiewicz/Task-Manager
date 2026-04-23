package com.lucc.taskmanager.repository;

import com.lucc.taskmanager.model.Status;
import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer>
{
    List<Task> findByUser(User user);

    long countByUserAndStatus(User user, Status status);

    long countByUserAndStatusAndDueDateBefore(User user, Status status, LocalDate date);

    long countByUserAndStatusAndCompletionDateBetween(User user, Status status, LocalDate start, LocalDate end);
}
