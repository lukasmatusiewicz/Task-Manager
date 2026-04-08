package com.lucc.taskmanager.controller;

import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController
{
    private final TaskService taskService;

    public TaskController(TaskService taskService)
    {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getTaskByUser(@AuthenticationPrincipal User user)
    {
        return taskService.getTasksByUser(user);
    }

    @PostMapping
    public Task addTask(@Valid @RequestBody Task task, @AuthenticationPrincipal User user)
    {
        return taskService.addTask(task, user);
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable int taskId, @Valid @RequestBody Task task, @AuthenticationPrincipal User user)
    {
        return taskService.updateTask(taskId, task, user);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable int taskId, @AuthenticationPrincipal User user)
    {
        taskService.deleteTask(taskId, user);
    }
}
