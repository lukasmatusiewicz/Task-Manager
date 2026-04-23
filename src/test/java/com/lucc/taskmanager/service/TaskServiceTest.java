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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(1, updatedInfo, user);

        assertEquals("Updated Title", result.getTitle());
        assertEquals(Status.DONE, result.getStatus());
        assertNotNull(result.getCompletionDate());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldSetCompletionDate_WhenStatusChangedToDone() {
        Task updatedInfo = new Task();
        updatedInfo.setStatus(Status.DONE);
        updatedInfo.setTitle("Test Task");
        updatedInfo.setDescription("Test Description");
        updatedInfo.setPriority(Priority.MEDIUM);

        when(taskRepository.findById(1)).thenReturn(Optional.of(task)); // current status is TODO
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(1, updatedInfo, user);

        assertEquals(Status.DONE, result.getStatus());
        assertNotNull(result.getCompletionDate());
        assertEquals(LocalDate.now(), result.getCompletionDate());
    }

    @Test
    void updateTask_ShouldClearCompletionDate_WhenStatusChangedFromDoneToTodo() {
        task.setStatus(Status.DONE);
        task.setCompletionDate(LocalDate.now().minusDays(1));

        Task updatedInfo = new Task();
        updatedInfo.setStatus(Status.TODO);
        updatedInfo.setTitle("Test Task");
        updatedInfo.setDescription("Test Description");
        updatedInfo.setPriority(Priority.MEDIUM);

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(1, updatedInfo, user);

        assertEquals(Status.TODO, result.getStatus());
        assertNull(result.getCompletionDate());
    }

    @Test
    void getDashboardStats_ShouldReturnCorrectStats() {
        when(taskRepository.countByUserAndStatus(eq(user), eq(Status.TODO))).thenReturn(5L);
        when(taskRepository.countByUserAndStatusAndDueDateBefore(eq(user), eq(Status.TODO), any(LocalDate.class))).thenReturn(2L);
        when(taskRepository.countByUserAndStatusAndCompletionDateBetween(eq(user), eq(Status.DONE), any(LocalDate.class), any(LocalDate.class))).thenReturn(3L);

        Map<String, Long> stats = taskService.getDashboardStats(user);

        assertEquals(5L, stats.get("pending"));
        assertEquals(2L, stats.get("overdue"));
        assertEquals(3L, stats.get("completedThisWeek"));
    }

    @Test
    void toggleTaskStatus_ShouldToggleFromTodoToDone() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task)); // current is TODO
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.toggleTaskStatus(1, user);

        assertEquals(Status.DONE, result.getStatus());
        assertNotNull(result.getCompletionDate());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void toggleTaskStatus_ShouldToggleFromDoneToTodo() {
        task.setStatus(Status.DONE);
        task.setCompletionDate(LocalDate.now());

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.toggleTaskStatus(1, user);

        assertEquals(Status.TODO, result.getStatus());
        assertNull(result.getCompletionDate());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenTaskExistsAndUserMatches() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        taskService.deleteTask(1, user);

        verify(taskRepository, times(1)).delete(task);
    }
}
