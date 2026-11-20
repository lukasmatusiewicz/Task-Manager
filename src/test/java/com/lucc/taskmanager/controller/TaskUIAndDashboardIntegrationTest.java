package com.lucc.taskmanager.controller;

import com.lucc.taskmanager.model.Priority;
import com.lucc.taskmanager.model.Role;
import com.lucc.taskmanager.model.Status;
import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.repository.TaskRepository;
import com.lucc.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskUIAndDashboardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User regularUser;
    private User otherUser;
    private User adminUser;
    private Task userTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        regularUser = userRepository.save(new User("alice", passwordEncoder.encode("password123"), "alice@example.com", Role.USER));
        otherUser = userRepository.save(new User("bob", passwordEncoder.encode("password123"), "bob@example.com", Role.USER));
        adminUser = userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "admin@example.com", Role.ADMIN));

        userTask = taskRepository.save(new Task("Alice Task", "Alice task description", regularUser, Status.TODO, Priority.HIGH, LocalDate.now().plusDays(2)));
    }

    @Test
    void restApi_ShouldReturn404JsonForMissingTaskId() throws Exception {
        int missingId = 999999;
        String validPayload = "{\"title\":\"Valid Title\",\"description\":\"Valid description\",\"status\":\"TODO\",\"priority\":\"LOW\"}";

        mockMvc.perform(put("/api/tasks/" + missingId)
                        .with(user(regularUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mockMvc.perform(patch("/api/tasks/" + missingId + "/toggle")
                        .with(user(regularUser))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mockMvc.perform(delete("/api/tasks/" + missingId)
                        .with(user(regularUser))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void restApi_ShouldReturn400JsonOnInvalidTaskRequest() throws Exception {
        String invalidPayload = "{\"title\":\"ab\",\"description\":\"Valid description\",\"status\":\"TODO\",\"priority\":\"LOW\"}";

        mockMvc.perform(post("/api/tasks")
                        .with(user(regularUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    void restApi_ShouldReturn409JsonWhenAdminCreatesDuplicateUsername() throws Exception {
        String duplicateUserPayload = "{\"username\":\"alice\",\"password\":\"secret123\",\"email\":\"dup@example.com\",\"role\":\"USER\"}";

        mockMvc.perform(post("/users")
                        .with(user(adminUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateUserPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void taskUIController_ShouldListAddEditAndDeleteTasks() throws Exception {
        mockMvc.perform(get("/tasks").with(user(regularUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks"))
                .andExpect(model().attributeExists("tasks", "newTask", "statuses", "priorities"));

        mockMvc.perform(post("/tasks/add")
                        .with(user(regularUser))
                        .with(csrf())
                        .param("title", "UI Added Task")
                        .param("description", "Added via form")
                        .param("status", "TODO")
                        .param("priority", "MEDIUM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        mockMvc.perform(post("/tasks/add")
                        .with(user(regularUser))
                        .with(csrf())
                        .param("title", "ab")
                        .param("description", "Too short title")
                        .param("status", "TODO")
                        .param("priority", "MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks"))
                .andExpect(model().attributeHasFieldErrors("newTask", "title"));

        mockMvc.perform(get("/tasks/edit/" + userTask.getId()).with(user(regularUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-task"))
                .andExpect(model().attributeExists("task", "statuses", "priorities"));

        mockMvc.perform(get("/tasks/edit/" + userTask.getId()).with(user(otherUser)))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error"));

        mockMvc.perform(get("/tasks/edit/999999").with(user(regularUser)))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"));

        mockMvc.perform(post("/tasks/edit/" + userTask.getId())
                        .with(user(regularUser))
                        .with(csrf())
                        .param("title", "Updated UI Task")
                        .param("description", "Updated description")
                        .param("status", "DONE")
                        .param("priority", "LOW"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        Task updated = taskRepository.findById(userTask.getId()).orElseThrow();
        assertEquals("Updated UI Task", updated.getTitle());
        assertEquals(Status.DONE, updated.getStatus());

        mockMvc.perform(post("/tasks/delete/" + userTask.getId())
                        .with(user(regularUser))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        assertFalse(taskRepository.existsById(userTask.getId()));
    }

    @Test
    void dashboardController_ShouldRenderDashboardWithStatsAndCharts() throws Exception {
        mockMvc.perform(get("/dashboard").with(user(regularUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("pending", "overdue", "completedThisWeek", "statusCounts", "priorityCounts"));
    }
}
