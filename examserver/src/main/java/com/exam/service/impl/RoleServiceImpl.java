package com.exam.service.impl;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exam.model.Role;
import com.exam.repo.RoleRepository;
import com.exam.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LogManager.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        logger.info("Creating role with ID: {} and name: {}", role.getRoleId(), role.getRoleName());

        // Check if role already exists
        Optional<Role> existingRole = roleRepository.findById(role.getRoleId());
        if (existingRole.isPresent()) {
            logger.warn("Role with ID: {} already exists. Returning existing role.", role.getRoleId());
            return existingRole.get();
        }

        Role savedRole = roleRepository.save(role);
        logger.info("Role created successfully with ID: {} and name: {}",
                savedRole.getRoleId(), savedRole.getRoleName());
        return savedRole;
    }

    @Override
    public Role getRoleById(Long roleId) {
        logger.debug("Fetching role by ID: {}", roleId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isPresent()) {
            logger.debug("Role found: {}", role.get().getRoleName());
        } else {
            logger.warn("Role not found with ID: {}", roleId);
        }
        return role.orElse(null);
    }

    @Override
    public Role getRoleByName(String roleName) {
        logger.debug("Fetching role by name: {}", roleName);
        Role role = roleRepository.findByRoleNameIgnoreCase(roleName);
        if (role != null) {
            logger.debug("Role found with ID: {}", role.getRoleId());
        } else {
            logger.warn("Role not found with name: {}", roleName);
        }
        return role;
    }

    @Override
    public List<Role> getAllRoles() {
        logger.debug("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        logger.info("Found {} roles in database", roles.size());
        return roles;
    }

    @Override
    public void initRoles() {
        logger.info("Initializing default roles...");

        // Define default roles
        Role normalRole = new Role(1L, "NORMAL");
        Role adminRole = new Role(2L, "ADMIN");
        Role instructorRole = new Role(3L, "INSTRUCTOR");

        // Check and create NORMAL role
        if (getRoleById(1L) == null) {
            createRole(normalRole);
            logger.info("Created default role: NORMAL");
        } else {
            logger.debug("Role NORMAL already exists, skipping creation");
        }

        // Check and create ADMIN role
        if (getRoleById(2L) == null) {
            createRole(adminRole);
            logger.info("Created default role: ADMIN");
        } else {
            logger.debug("Role ADMIN already exists, skipping creation");
        }

        // Check and create INSTRUCTOR role
        if (getRoleById(3L) == null) {
            createRole(instructorRole);
            logger.info("Created default role: INSTRUCTOR");
        } else {
            logger.debug("Role INSTRUCTOR already exists, skipping creation");
        }

        logger.info("Role initialization completed");
    }
}
