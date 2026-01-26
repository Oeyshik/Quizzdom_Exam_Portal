package com.exam.service;

import java.util.List;
import com.exam.model.Role;

public interface RoleService {
    // Create a new role
    Role createRole(Role role);
    
    // Get role by ID
    Role getRoleById(Long roleId);
    
    // Get role by name
    Role getRoleByName(String roleName);
    
    // Get all roles
    List<Role> getAllRoles();
    
    // Initialize default roles
    void initRoles();
}

