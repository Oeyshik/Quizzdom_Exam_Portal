package com.exam.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exam.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Find role by name (case-insensitive)
    Role findByRoleNameIgnoreCase(String roleName);
}
