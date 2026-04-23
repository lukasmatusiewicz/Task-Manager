package com.lucc.taskmanager.controller;

import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get all tasks for the logged-in user", description = "Retrieves a list of all tasks associated with the currently authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
        @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource", content = @Content)
    })
    public List<Task> getTaskByUser(@AuthenticationPrincipal @Parameter(hidden = true) User user)
    {
        return taskService.getTasksByUser(user);
    }

    @PostMapping
    @Operation(summary = "Add a new task", description = "Creates a new task for the currently authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created the task", content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "401", description = "You are not authorized to create a task", content = @Content)
    })
    public Task addTask(@Valid @RequestBody Task task, @AuthenticationPrincipal @Parameter(hidden = true) User user)
    {
        return taskService.addTask(task, user);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update an existing task", description = "Updates the details of an existing task identified by its ID. Users can only update their own tasks.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the task", content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "400", description = "Invalid task ID or input data", content = @Content),
        @ApiResponse(responseCode = "401", description = "You are not authorized to update this task", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public Task updateTask(@PathVariable int taskId, @Valid @RequestBody Task task, @AuthenticationPrincipal @Parameter(hidden = true) User user)
    {
        return taskService.updateTask(taskId, task, user);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a task by ID", description = "Removes a task from the system identified by its ID. Users can only delete their own tasks.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted the task"),
        @ApiResponse(responseCode = "401", description = "You are not authorized to delete this task", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public void deleteTask(@PathVariable int taskId, @AuthenticationPrincipal @Parameter(hidden = true) User user)
    {
        taskService.deleteTask(taskId, user);
    }

    @PatchMapping("/{taskId}/toggle")
    @Operation(summary = "Toggle task status", description = "Toggles the status of a task between 'TODO' and 'DONE'. Also manages the 'completionDate' automatically.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully toggled task status", content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "401", description = "You are not authorized to toggle this task", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public Task toggleTaskStatus(@PathVariable int taskId, @AuthenticationPrincipal @Parameter(hidden = true) User user)
    {
        return taskService.toggleTaskStatus(taskId, user);
    }
}
