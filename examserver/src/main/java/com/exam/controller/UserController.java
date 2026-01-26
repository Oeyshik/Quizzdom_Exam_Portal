package com.exam.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.exam.model.User;
import com.exam.model.Role;
import com.exam.model.UserRole;
import com.exam.service.UserService;
import com.exam.service.RoleService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")

public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    // Creating User
    @PostMapping("/")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        logger.info("Received request to create user with username: {}", user.getUsername());
        logger.debug("User details - Email: {}, FirstName: {}, LastName: {}",
                user.getEmail(), user.getFirstName(), user.getLastName());

        try {
            // Get the NORMAL role from database (or create if doesn't exist)
            Role normalRole = roleService.getRoleByName("NORMAL");
            if (normalRole == null) {
                logger.warn("NORMAL role not found. Creating it now...");
                normalRole = new Role(1L, "NORMAL");
                normalRole = roleService.createRole(normalRole);
            }

            Set<UserRole> roles = new HashSet<>();
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(normalRole);
            roles.add(userRole);

            logger.debug("Creating user with role: {}", normalRole.getRoleName());
            User createdUser = this.userService.createUser(user, roles);
            logger.info("User created successfully with ID: {} and username: {}",
                    createdUser.getId(), createdUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            logger.error("Error creating user with username: {}. Error: {}",
                    user.getUsername(), e.getMessage(), e);
            // Handle duplicate user or other exceptions
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "User registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    // Check if username is taken (must be before /{username} to avoid route
    // conflict)
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable("username") String username) {
        logger.debug("Checking if username is taken: {}", username);
        User user = this.userService.getUser(username);
        boolean isTaken = user != null;
        logger.info("Username '{}' availability check result: {}", username, isTaken ? "TAKEN" : "AVAILABLE");
        return ResponseEntity.ok(isTaken);
    }

    @GetMapping("/{username}")
    public User getUser(@PathVariable("username") String username) {
        logger.info("Fetching user by username: {}", username);
        User user = this.userService.getUser(username);
        if (user != null) {
            logger.debug("User found with ID: {} and email: {}", user.getId(), user.getEmail());
        } else {
            logger.warn("User not found with username: {}", username);
        }
        return user;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        try {
            this.userService.deleteUser(userId);
            logger.info("User with ID: {} deleted successfully", userId);
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}. Error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<String> updateUserByUsername(@PathVariable("username") String username,
            @RequestBody User updatedUser) {
        logger.info("Updating user with username: {}", username);
        logger.debug("Updated user details - Email: {}, FirstName: {}, LastName: {}",
                updatedUser.getEmail(), updatedUser.getFirstName(), updatedUser.getLastName());
        try {
            // Call the service layer method to update the user by username
            userService.updateUserByUsername(username, updatedUser);
            logger.info("User with username: {} updated successfully", username);
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating user with username: {}. Error: {}", username, e.getMessage(), e);
            // Handle exceptions if any
            return new ResponseEntity<>("Failed to update user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
