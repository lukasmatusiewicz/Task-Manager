package com.lucc.taskmanager.controller;

import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.service.UserService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Operations related to users management")
public class UserController
{
    private final UserService userService;

    @Autowired
    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users (Admin only)")
    public List<User> getUsers()
    {
        return userService.getUsers();
    }

    @PostMapping
    @Operation(summary = "Add a new user (Admin only)")
    public User addUser(@Valid @RequestBody User user)
    {
        return userService.addUser(user);
    }
}

