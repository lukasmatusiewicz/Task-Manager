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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityAndControllerIntegrationTest {

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
    private Task regularUserTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        regularUser = userRepository.save(new User("alice", passwordEncoder.encode("password123"), "alice@example.com", Role.USER));
        otherUser = userRepository.save(new User("bob", passwordEncoder.encode("password123"), "bob@example.com", Role.USER));
        adminUser = userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "admin@example.com", Role.ADMIN));

        regularUserTask = taskRepository.save(new Task("Alice Task", "Alice task description", regularUser, Status.TODO, Priority.MEDIUM));
    }

    @Test
    void unauthenticatedAccessToProtectedRoutes_ShouldBeBlocked() throws Exception {
        mockMvc.perform(get("/api/tasks")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/dashboard")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/tasks")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/users")).andExpect(status().is3xxRedirection());
    }

    @Test
    void usersEndpoint_ShouldReturn403ForUserAnd200ForAdminWithoutPassword() throws Exception {
        mockMvc.perform(get("/users").with(user(regularUser)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/users").with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("password"))));
    }

    @Test
    void register_ShouldIgnoreAdminRoleEscalationAndHandleDuplicateUsername() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "charlie")
                        .param("email", "charlie@example.com")
                        .param("password", "secret123")
                        .param("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        User created = userRepository.findByUsername("charlie").orElseThrow();
        assertEquals(Role.USER, created.getRole());

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "charlie")
                        .param("email", "charlie2@example.com")
                        .param("password", "secret123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("user", "username"));
    }

    @Test
    void getApiTasks_ShouldNotExposeUserOrPasswordFields() throws Exception {
        mockMvc.perform(get("/api/tasks").with(user(regularUser)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Alice Task")))
                .andExpect(content().string(not(containsString("password"))))
                .andExpect(content().string(not(containsString("alice@example.com"))));
    }

    @Test
    void crossUserTaskOperations_ShouldBeRejected() throws Exception {
        String updatePayload = "{\"title\":\"Hacked\",\"description\":\"Hacked desc\",\"status\":\"DONE\",\"priority\":\"HIGH\"}";

        mockMvc.perform(put("/api/tasks/" + regularUserTask.getId())
                        .with(user(otherUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mockMvc.perform(patch("/api/tasks/" + regularUserTask.getId() + "/toggle")
                        .with(user(otherUser))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mockMvc.perform(delete("/api/tasks/" + regularUserTask.getId())
                        .with(user(otherUser))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        Task persisted = taskRepository.findById(regularUserTask.getId()).orElseThrow();
        assertEquals("Alice Task", persisted.getTitle());
        assertEquals(Status.TODO, persisted.getStatus());
        assertTrue(taskRepository.existsById(regularUserTask.getId()));
    }

    @Test
    void apiTasksMutations_ShouldRequireCsrfToken() throws Exception {
        String createPayload = "{\"title\":\"New Task\",\"description\":\"Valid description\",\"status\":\"TODO\",\"priority\":\"LOW\"}";

        mockMvc.perform(post("/api/tasks")
                        .with(user(regularUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/tasks/" + regularUserTask.getId() + "/toggle")
                        .with(user(regularUser)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/tasks/" + regularUserTask.getId())
                        .with(user(regularUser)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/tasks")
                        .with(user(regularUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("New Task")));
    }

    @Test
    void actuatorHealth_ShouldReturn200WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(get("/actuator/env"))
                .andExpect(status().is3xxRedirection());
    }
}
