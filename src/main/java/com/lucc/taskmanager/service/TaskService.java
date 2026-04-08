package com.lucc.taskmanager.service;

import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService
{
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository)
    {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasksByUser(User user)
    {
        return taskRepository.findByUser(user);
    }

    public Task addTask(Task task, User user)
    {
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Task getTaskById(int taskId, User user)
    {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("No such task with id: " + taskId));

        if(!task.getUser().getUsername().equals(user.getUsername()))
            throw new IllegalArgumentException("You can only access your own tasks.");

        return task;
    }

    public Task updateTask(int taskId, Task updatedTask, User user)
    {
        Task existingTask = getTaskById(taskId, user);
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());
        return taskRepository.save(existingTask);
    }

    public void deleteTask(int taskId, User user)
    {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("No such task with id: " + taskId));

        if(!task.getUser().getUsername().equals(user.getUsername()))
            throw new IllegalArgumentException("You can only delete your own tasks.");

        taskRepository.delete(task);
    }
}
