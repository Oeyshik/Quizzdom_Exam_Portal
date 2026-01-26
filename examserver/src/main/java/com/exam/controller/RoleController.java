package com.exam.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.model.Role;
import com.exam.service.RoleService;

@RestController
@RequestMapping("/role")
@CrossOrigin("*")
public class RoleController {

    private static final Logger logger = LogManager.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    // Create a new role
    @PostMapping("/")
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        logger.info("Received request to create role with name: {}", role.getRoleName());
        
        try {
            if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
                logger.warn("Role creation failed - role name is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Role name is required");
            }
            
            Role createdRole = roleService.createRole(role);
            logger.info("Role created successfully with ID: {} and name: {}", 
                       createdRole.getRoleId(), createdRole.getRoleName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
        } catch (Exception e) {
            logger.error("Error creating role: {}. Error: {}", role.getRoleName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create role: " + e.getMessage());
        }
    }

    // Get all roles
    @GetMapping("/")
    public ResponseEntity<List<Role>> getAllRoles() {
        logger.info("Fetching all roles");
        List<Role> roles = roleService.getAllRoles();
        logger.info("Returning {} roles", roles.size());
        return ResponseEntity.ok(roles);
    }

    // Get role by ID
    @GetMapping("/{roleId}")
    public ResponseEntity<?> getRoleById(@PathVariable("roleId") Long roleId) {
        logger.info("Fetching role by ID: {}", roleId);
        Role role = roleService.getRoleById(roleId);
        
        if (role != null) {
            logger.debug("Role found: {}", role.getRoleName());
            return ResponseEntity.ok(role);
        } else {
            logger.warn("Role not found with ID: {}", roleId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Role not found with ID: " + roleId);
        }
    }

    // Get role by name
    @GetMapping("/name/{roleName}")
    public ResponseEntity<?> getRoleByName(@PathVariable("roleName") String roleName) {
        logger.info("Fetching role by name: {}", roleName);
        Role role = roleService.getRoleByName(roleName);
        
        if (role != null) {
            logger.debug("Role found with ID: {}", role.getRoleId());
            return ResponseEntity.ok(role);
        } else {
            logger.warn("Role not found with name: {}", roleName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Role not found with name: " + roleName);
        }
    }

    // Initialize default roles
    @PostMapping("/init")
    public ResponseEntity<String> initializeRoles() {
        logger.info("Received request to initialize default roles");
        try {
            roleService.initRoles();
            return ResponseEntity.ok("Default roles initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing roles. Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initialize roles: " + e.getMessage());
        }
    }
}

