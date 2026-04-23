package com.lucc.taskmanager.controller;

import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.service.UserService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get all users", description = "Retrieves a comprehensive list of all registered users in the system. This endpoint is restricted to users with the ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required", content = @Content)
    })
    public List<User> getUsers()
    {
        return userService.getUsers();
    }

    @PostMapping
    @Operation(summary = "Add a new user", description = "Creates a new user account. This endpoint is restricted to users with the ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created the user", content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user data provided", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required", content = @Content)
    })
    public User addUser(@Valid @RequestBody User user)
    {
        return userService.addUser(user);
    }
}

