package com.lucc.taskmanager.init;

import com.lucc.taskmanager.model.Priority;
import com.lucc.taskmanager.model.Role;
import com.lucc.taskmanager.model.Status;
import com.lucc.taskmanager.model.Task;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.repository.TaskRepository;
import com.lucc.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner
{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public DataLoader(PasswordEncoder passwordEncoder, UserRepository userRepository, TaskRepository taskRepository)
    {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String ... args) throws Exception
    {
        if (userRepository.count() == 0) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@gmail.com", Role.ADMIN);
            User user = new User("user", passwordEncoder.encode("user123"), "user@gmail.com", Role.USER);

            userRepository.saveAll(List.of(admin, user));
            System.out.println("Users loaded");

            LocalDate today = LocalDate.now();

            Task adminTask1 = new Task("Pizza", "Pizza description", admin, Status.TODO, Priority.MEDIUM, today.plusDays(2));
            Task adminTask2 = new Task("Burger", "Burger description", admin, Status.TODO, Priority.HIGH, today.minusDays(1)); // Overdue
            Task adminTask3 = new Task("Pasta", "Pasta description", admin, Status.DONE, Priority.LOW, today.minusDays(2));
            adminTask3.setCompletionDate(today.minusDays(1)); // Completed this week
            
            Task adminTask4 = new Task("Sandwich", "Sandwich description", admin, Status.TODO, Priority.MEDIUM, today.plusDays(5));
            Task adminTask5 = new Task("Coffee", "Coffee description", admin, Status.TODO, Priority.MEDIUM, today.plusDays(1));
            Task adminTask6 = new Task("Coffee Shaker", "Coffee shaker description", admin, Status.TODO, Priority.MEDIUM, today.plusDays(3));
            Task adminTask7 = new Task("Coffee Mug", "Coffee mug description", admin, Status.DONE, Priority.MEDIUM, today.minusDays(10));
            adminTask7.setCompletionDate(today.minusDays(8)); // Completed more than a week ago

            Task userTask1 = new Task("Study Spring Boot", "Learn about Spring Boot security and data.", user, Status.TODO, Priority.HIGH, today.plusDays(3));
            Task userTask2 = new Task("Workout", "Go to the gym for 1 hour.", user, Status.TODO, Priority.MEDIUM, today.minusDays(2)); // Overdue
            Task userTask3 = new Task("Buy Groceries", "Buy milk, eggs, and bread.", user, Status.DONE, Priority.LOW, today.minusDays(1));
            userTask3.setCompletionDate(today); // Completed today
            
            Task userTask4 = new Task("Read a Book", "Read at least 30 pages.", user, Status.TODO, Priority.LOW, today.plusDays(7));
            Task userTask5 = new Task("Clean the Room", "Deep clean the bedroom.", user, Status.TODO, Priority.MEDIUM, today.plusDays(1));

            taskRepository.saveAll(List.of(adminTask1, adminTask2, adminTask3, adminTask4, adminTask5, adminTask6, adminTask7, userTask1, userTask2, userTask3, userTask4, userTask5));
            System.out.println("Tasks loaded");
        }
    }
}
