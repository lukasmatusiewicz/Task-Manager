package com.lucc.taskmanager.repository;

import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer>
{
    List<Task> findByUser(User user);
}
