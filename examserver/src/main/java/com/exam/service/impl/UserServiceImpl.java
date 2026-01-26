package com.exam.service.impl;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.exam.model.User;
import com.exam.model.UserRole;
import com.exam.repo.RoleRepository;
import com.exam.repo.UserRepository;
import com.exam.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Creating User
    @Override
    public User createUser(User user, Set<UserRole> userRoles) throws Exception {
        logger.debug("Attempting to create user with username: {}", user.getUsername());

        User local = this.userRepository.findByUsername(user.getUsername());
        if (local != null) {
            logger.warn("User creation failed - username '{}' already exists", user.getUsername());
            throw new Exception("User is already present...");
        } else {
            logger.debug("Username '{}' is available. Proceeding with user creation.", user.getUsername());
            
            // Encode password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            logger.debug("Password encoded successfully");
            
            // Create User
            // Note: Roles should already exist in database, we just link them via UserRole
            for (UserRole ur : userRoles) {
                logger.debug("Linking user to role: {} (ID: {})",
                        ur.getRole().getRoleName(), ur.getRole().getRoleId());
                // Ensure role exists in database (should already exist from initialization)
                if (roleRepository.findById(ur.getRole().getRoleId()).isEmpty()) {
                    logger.warn("Role {} not found in database, creating it...", ur.getRole().getRoleName());
                    roleRepository.save(ur.getRole());
                }
            }

            user.getUserRoles().addAll(userRoles);
            local = this.userRepository.save(user);
            logger.info("User created successfully with ID: {} and username: {}",
                    local.getId(), local.getUsername());
        }

        return local;
    }

    // getting user by username
    @Override
    public User getUser(String username) {
        logger.debug("Fetching user by username: {}", username);
        User user = this.userRepository.findByUsername(username);
        if (user != null) {
            logger.debug("User found with ID: {} and email: {}", user.getId(), user.getEmail());
        } else {
            logger.debug("No user found with username: {}", username);
        }
        return user;
    }

    // deleting user by Id
    @Override
    public void deleteUser(Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        try {
            this.userRepository.deleteById(userId);
            logger.info("User with ID: {} deleted successfully", userId);
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}. Error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    // updating user by username
    @Override
    public void updateUserByUsername(String username, User updatedUser) {
        logger.debug("Updating user with username: {}", username);
        User existingUser = userRepository.findByUsername(username);

        if (existingUser != null) {
            logger.debug("User found. Updating fields for user ID: {}", existingUser.getId());
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setProfile(updatedUser.getProfile());

            // Save the updated user back to the database
            userRepository.save(existingUser);
            logger.info("User with username: {} updated successfully", username);
        } else {
            logger.warn("Update failed - User not found with username: {}", username);
        }
    }

}
