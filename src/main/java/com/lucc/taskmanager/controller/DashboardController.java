package com.lucc.taskmanager.controller;

import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.service.TaskService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final TaskService taskService;

    public DashboardController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String getDashboard(@AuthenticationPrincipal User user, Model model) {
        Map<String, Long> stats = taskService.getDashboardStats(user);
        model.addAllAttributes(stats);

        List<Task> tasks = taskService.getTasksByUser(user);
        
        // Data for status chart
        Map<String, Long> statusCounts = tasks.stream()
                .collect(Collectors.groupingBy(t -> t.getStatus().name(), Collectors.counting()));
        model.addAttribute("statusCounts", statusCounts);

        // Data for priority chart
        Map<String, Long> priorityCounts = tasks.stream()
                .collect(Collectors.groupingBy(t -> t.getPriority().name(), Collectors.counting()));
        model.addAttribute("priorityCounts", priorityCounts);

        return "dashboard";
    }
}
