package com.lucc.taskmanager.init;

import com.lucc.taskmanager.model.Role;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.repository.UserRepository;
import org.jspecify.annotations.NonNull;
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

    @Autowired
    public DataLoader(PasswordEncoder passwordEncoder, UserRepository userRepository)
    {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String ... args) throws Exception
    {
        if (userRepository.count() == 0) {
        User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@gmail.com", Role.ADMIN);
        User user = new User("user", passwordEncoder.encode("user123"), "user@gmail.com", Role.USER);
        userRepository.saveAll(List.of(admin, user));
        }
    }
}
