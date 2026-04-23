package com.lucc.taskmanager.service;

import com.lucc.taskmanager.model.Status;
import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (task.getStatus() == Status.DONE && task.getCompletionDate() == null) {
            task.setCompletionDate(LocalDate.now());
        }
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

        // Handle completion date logic
        if (existingTask.getStatus() != Status.DONE && updatedTask.getStatus() == Status.DONE) {
            existingTask.setCompletionDate(LocalDate.now());
        } else if (existingTask.getStatus() == Status.DONE && updatedTask.getStatus() != Status.DONE) {
            existingTask.setCompletionDate(null);
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setDueDate(updatedTask.getDueDate());
        return taskRepository.save(existingTask);
    }

    public void deleteTask(int taskId, User user)
    {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("No such task with id: " + taskId));

        if(!task.getUser().getUsername().equals(user.getUsername()))
            throw new IllegalArgumentException("You can only delete your own tasks.");

        taskRepository.delete(task);
    }

    public Map<String, Long> getDashboardStats(User user) {
        LocalDate now = LocalDate.now();
        LocalDate weekAgo = now.minusDays(7);

        Map<String, Long> stats = new HashMap<>();
        stats.put("pending", taskRepository.countByUserAndStatus(user, Status.TODO));
        stats.put("overdue", taskRepository.countByUserAndStatusAndDueDateBefore(user, Status.TODO, now));
        stats.put("completedThisWeek", taskRepository.countByUserAndStatusAndCompletionDateBetween(user, Status.DONE, weekAgo, now));

        return stats;
    }

    public Task toggleTaskStatus(int taskId, User user) {
        Task task = getTaskById(taskId, user);
        if (task.getStatus() == Status.TODO) {
            task.setStatus(Status.DONE);
            task.setCompletionDate(LocalDate.now());
        } else {
            task.setStatus(Status.TODO);
            task.setCompletionDate(null);
        }
        return taskRepository.save(task);
    }
}
