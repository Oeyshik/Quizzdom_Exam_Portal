package com.exam.service;

import com.exam.model.User;
import com.exam.model.UserRole;
import java.util.Set;

public interface UserService {
    // Creating USer
    public User createUser(User user, Set<UserRole> userRoles) throws Exception;

    // Get user by username
    public User getUser(String username);

    // Delete user by Id
    public void deleteUser(Long userId);

    // Update user by username
    void updateUserByUsername(String username, User updatedUser);

}
