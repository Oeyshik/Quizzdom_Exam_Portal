package com.exam.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.model.PasswordResetRequest;
import com.exam.model.User;
import com.exam.repo.UserRepository;

@RestController
@RequestMapping("/password")
@CrossOrigin("*")
public class PasswordResetController {

    private static final Logger logger = LogManager.getLogger(PasswordResetController.class);

    // In-memory storage for reset tokens (in production, use Redis or database)
    private static final Map<String, PasswordResetToken> resetTokens = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Request password reset
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequest request) {
        logger.info("Password reset request received for username: {}", request.getUsername());

        try {
            User user = userRepository.findByUsername(request.getUsername());
            
            if (user == null) {
                logger.warn("Password reset failed - user not found: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found", "message", "Username does not exist"));
            }

            // Verify email matches (optional - you can remove this check if needed)
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                logger.warn("Password reset failed - email mismatch for user: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid email", "message", "Email does not match"));
            }

            // Generate reset token
            String resetToken = UUID.randomUUID().toString();
            resetTokens.put(resetToken, new PasswordResetToken(request.getUsername(), System.currentTimeMillis()));

            logger.info("Password reset token generated for user: {}", request.getUsername());
            
            // In production, send email with reset link
            // For now, return token (remove this in production!)
            return ResponseEntity.ok(Map.of(
                "message", "Password reset token generated",
                "resetToken", resetToken, // Remove this in production - only for testing
                "username", request.getUsername()
            ));

        } catch (Exception e) {
            logger.error("Error processing password reset request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process request", "message", e.getMessage()));
        }
    }

    // Reset password with token
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        logger.info("Password reset attempt with token for user: {}", request.getUsername());

        try {
            if (request.getResetToken() == null || request.getResetToken().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid token", "message", "Reset token is required"));
            }

            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid password", "message", "New password is required"));
            }

            // Validate token
            PasswordResetToken tokenData = resetTokens.get(request.getResetToken());
            if (tokenData == null) {
                logger.warn("Invalid reset token used");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid token", "message", "Reset token is invalid or expired"));
            }

            // Check token expiration (24 hours)
            long tokenAge = System.currentTimeMillis() - tokenData.getTimestamp();
            if (tokenAge > 24 * 60 * 60 * 1000) {
                logger.warn("Expired reset token used");
                resetTokens.remove(request.getResetToken());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Expired token", "message", "Reset token has expired"));
            }

            // Verify username matches token
            if (!tokenData.getUsername().equals(request.getUsername())) {
                logger.warn("Username mismatch for reset token");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid request", "message", "Username does not match token"));
            }

            // Find user and update password
            User user = userRepository.findByUsername(request.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found", "message", "User does not exist"));
            }

            // Validate password strength (same as signup)
            String passwordPattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$";
            if (!request.getNewPassword().matches(passwordPattern)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Weak password", 
                            "message", "Password must be at least 8 characters with uppercase, lowercase, number, and special character"));
            }

            // Encode and update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            // Remove used token
            resetTokens.remove(request.getResetToken());

            logger.info("Password reset successful for user: {}", request.getUsername());
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));

        } catch (Exception e) {
            logger.error("Error resetting password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reset password", "message", e.getMessage()));
        }
    }

    // Inner class to store reset token data
    private static class PasswordResetToken {
        private String username;
        private long timestamp;

        public PasswordResetToken(String username, long timestamp) {
            this.username = username;
            this.timestamp = timestamp;
        }

        public String getUsername() {
            return username;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}

