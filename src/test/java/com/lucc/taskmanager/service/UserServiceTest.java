package com.lucc.taskmanager.service;

import com.lucc.taskmanager.dto.RegisterRequest;
import com.lucc.taskmanager.model.Role;
import com.lucc.taskmanager.model.User;
import com.lucc.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
    }

    @Test
    void getUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<User> users = userService.getUsers();

        assertEquals(1, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void addUser_ShouldEncryptPasswordAndSaveUser() {
        when(passwordEncoder.encode("password")).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.addUser(user);

        assertNotNull(savedUser);
        assertEquals(Role.USER, user.getRole()); // Default role
        assertEquals("encryptedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("unknown"));
    }

    @Test
    void addUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.addUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldAlwaysAssignUserRole() {
        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "secret123");
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("encodedSecret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registered = userService.registerUser(request);

        assertEquals("newuser", registered.getUsername());
        assertEquals(Role.USER, registered.getRole());
        assertEquals("encodedSecret", registered.getPassword());
    }
}
