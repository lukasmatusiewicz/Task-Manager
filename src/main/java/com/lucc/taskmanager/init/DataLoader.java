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

            Task adminTask1 = new Task("Pizza", "Pizza is a dish of Italian origin made by a mixture of fried bread and tomatoes, usually sliced thinly and topped with a slice of cheese.", admin, Status.TODO, Priority.MEDIUM);
            Task adminTask2 = new Task("Burger", "A burger is a large, flat, round, steak dressed in lettuce, tomato, cheese, and meat sauce.", admin, Status.TODO, Priority.MEDIUM);
            Task adminTask3 = new Task("Pasta", "Pasta is a food made from a mixture of flour, eggs, and water, typically cooked under high heat.", admin, Status.TODO, Priority.MEDIUM);
            Task adminTask4 = new Task("Sandwich", "A sandwich is a flat bread with lettuce and tomato on one side, and a slice of cheese on the other side.", admin, Status.TODO, Priority.MEDIUM);
            Task adminTask5 = new Task("Coffee", "Coffee is a brewed drink made from roasted coffee beans, ground coffee, and sugar.", admin, Status.TODO, Priority.MEDIUM);
            Task adminTask6 = new Task("Coffee Shaker", "A coffee shaker is a container for storing and shaking coffee.", admin, Status.TODO, Priority.MEDIUM);
            Task adminTask7 = new Task("Coffee Mug", "A coffee mug is a container for storing and shaking coffee.", admin, Status.TODO, Priority.MEDIUM);


            Task userTask1 = new Task("Pizza", "Pizza is a dish of Italian origin made by a mixture of fried bread and tomatoes, usually sliced thinly and topped with a slice of cheese.", user, Status.TODO, Priority.MEDIUM);
            Task userTask2 = new Task("Burger", "A burger is a large, flat, round, steak dressed in lettuce, tomato, cheese, and meat sauce.", user, Status.TODO, Priority.MEDIUM);
            Task userTask3 = new Task("Pasta", "Pasta is a food made from a mixture of flour, eggs, and water, typically cooked under high heat.", user, Status.TODO, Priority.MEDIUM);
            Task userTask4 = new Task("Sandwich", "A sandwich is a flat bread with lettuce and tomato on one side, and a slice of cheese on the other side.", user, Status.TODO, Priority.MEDIUM);
            Task userTask5 = new Task("Coffee", "Coffee is a brewed drink made from roasted coffee beans, ground coffee, and sugar.", user, Status.TODO, Priority.MEDIUM);

            taskRepository.saveAll(List.of(adminTask1, adminTask2, adminTask3, adminTask4, adminTask5, adminTask6, adminTask7, userTask1, userTask2, userTask3, userTask4, userTask5));
            System.out.println("Tasks loaded");
        }
    }
}
