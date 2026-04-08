package com.lucc.taskmanager.controller;

import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Operations related to tasks management")
public class TaskController
{
    private final TaskService taskService;

    public TaskController(TaskService taskService)
    {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Get all tasks for the logged-in user")
    public List<Task> getTaskByUser(@AuthenticationPrincipal @Parameter(hidden = true) User user)
    {
        return taskService.getTasksByUser(user);
    }

    @PostMapping
    @Operation(summary = "Add a new task for the logged-in user")
    public Task addTask(@Valid @RequestBody Task task, @AuthenticationPrincipal @Parameter(hidden = true) User user)
    {
        return taskService.addTask(task, user);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update an existing task")
    public Task updateTask(@PathVariable int taskId, @Valid @RequestBody Task task, @AuthenticationPrincipal @Parameter(hidden = true) User user)
    {
        return taskService.updateTask(taskId, task, user);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a task by ID")
    public void deleteTask(@PathVariable int taskId, @AuthenticationPrincipal @Parameter(hidden = true) User user)
    {
        taskService.deleteTask(taskId, user);
    }
}
