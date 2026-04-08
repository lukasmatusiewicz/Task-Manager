package com.lucc.taskmanager.service;

import com.lucc.taskmanager.model.Priority;
import com.lucc.taskmanager.model.Status;
import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");

        task = new Task();
        task.setId(1);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUser(user);
        task.setStatus(Status.TODO);
        task.setPriority(Priority.MEDIUM);
    }

    @Test
    void getTasksByUser_ShouldReturnTasks() {
        when(taskRepository.findByUser(user)).thenReturn(Arrays.asList(task));

        List<Task> tasks = taskService.getTasksByUser(user);

        assertEquals(1, tasks.size());
        assertEquals(task.getTitle(), tasks.get(0).getTitle());
        verify(taskRepository, times(1)).findByUser(user);
    }

    @Test
    void addTask_ShouldSaveTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.addTask(new Task(), user);

        assertNotNull(savedTask);
        assertEquals(user, savedTask.getUser());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenTaskExistsAndUserMatches() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTaskById(1, user);

        assertNotNull(foundTask);
        assertEquals(1, foundTask.getId());
    }

    @Test
    void getTaskById_ShouldThrowException_WhenTaskDoesNotExist() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.getTaskById(1, user));
    }

    @Test
    void getTaskById_ShouldThrowException_WhenUserDoesNotMatch() {
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class, () -> taskService.getTaskById(1, otherUser));
    }

    @Test
    void updateTask_ShouldUpdateAndSaveTask() {
        Task updatedInfo = new Task();
        updatedInfo.setTitle("Updated Title");
        updatedInfo.setDescription("Updated Description");
        updatedInfo.setStatus(Status.DONE);
        updatedInfo.setPriority(Priority.HIGH);

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.updateTask(1, updatedInfo, user);

        assertEquals("Updated Title", result.getTitle());
        assertEquals(Status.DONE, result.getStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenTaskExistsAndUserMatches() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        taskService.deleteTask(1, user);

        verify(taskRepository, times(1)).delete(task);
    }
}
