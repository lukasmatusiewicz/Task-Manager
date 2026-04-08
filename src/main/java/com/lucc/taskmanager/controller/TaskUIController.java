package com.lucc.taskmanager.controller;

import com.lucc.taskmanager.model.Priority;
import com.lucc.taskmanager.model.Status;
import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskUIController {
    private final TaskService taskService;

    public TaskUIController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String getTasks(@AuthenticationPrincipal User user, Model model) {
        List<Task> tasks = taskService.getTasksByUser(user);
        model.addAttribute("tasks", tasks);
        if (!model.containsAttribute("newTask")) {
            model.addAttribute("newTask", new Task()); // For the form
        }
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());
        return "tasks"; // Returns tasks.html
    }

    @PostMapping("/add")
    public String addTask(@Valid @ModelAttribute("newTask") Task task, BindingResult bindingResult, @AuthenticationPrincipal User user, Model model) {
        if (bindingResult.hasErrors()) {
            List<Task> tasks = taskService.getTasksByUser(user);
            model.addAttribute("tasks", tasks);
            model.addAttribute("statuses", Status.values());
            model.addAttribute("priorities", Priority.values());
            return "tasks";
        }
        taskService.addTask(task, user);
        return "redirect:/tasks"; // Refresh the page
    }

    @GetMapping("/edit/{taskId}")
    public String showEditForm(@PathVariable int taskId, @AuthenticationPrincipal User user, Model model) {
        Task task = taskService.getTaskById(taskId, user);
        model.addAttribute("task", task);
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());
        return "edit-task";
    }

    @PostMapping("/edit/{taskId}")
    public String updateTask(@PathVariable int taskId, @Valid @ModelAttribute("task") Task task, BindingResult bindingResult, @AuthenticationPrincipal User user, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", Status.values());
            model.addAttribute("priorities", Priority.values());
            return "edit-task";
        }
        taskService.updateTask(taskId, task, user);
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{taskId}")
    public String deleteTask(@PathVariable int taskId, @AuthenticationPrincipal User user) {
        taskService.deleteTask(taskId, user);
        return "redirect:/tasks"; // Refresh the page
    }
}
