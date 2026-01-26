package com.exam.controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.model.JwtRequest;
import com.exam.model.JwtResponse;
import com.exam.model.User;
import com.exam.repo.UserRepository;
import com.exam.util.JwtUtil;

@RestController
@CrossOrigin("*")
@RequestMapping("/authenticate")
public class AuthenticateController {

    private static final Logger logger = LogManager.getLogger(AuthenticateController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // Generate token
    @PostMapping("/")
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        logger.info("Authentication request received for username: {}", jwtRequest.getUsername());

        try {
            authenticate(jwtRequest.getUsername(), jwtRequest.getPassword());

            final UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtRequest.getUsername());
            final String token = this.jwtUtil.generateToken(userDetails);

            // Get user details including roles
            User user = this.userRepository.findByUsername(jwtRequest.getUsername());
            if (user == null) {
                logger.error("User not found after authentication: {}", jwtRequest.getUsername());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User not found", "message", "User data not available"));
            }

            Set<String> roles = new HashSet<>();
            if (user.getUserRoles() != null) {
                roles = user.getUserRoles().stream()
                        .map(ur -> ur.getRole().getRoleName())
                        .collect(Collectors.toSet());
            }

            // Create response with token, user details, and roles
            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setToken(token);
            jwtResponse.setUsername(user.getUsername());
            jwtResponse.setFirstName(user.getFirstName());
            jwtResponse.setLastName(user.getLastName());
            jwtResponse.setEmail(user.getEmail());
            jwtResponse.setRoles(roles);
            jwtResponse.setId(user.getId());

            logger.info("Authentication successful for username: {} with roles: {}", 
                       jwtRequest.getUsername(), roles);
            return ResponseEntity.ok(jwtResponse);

        } catch (Exception e) {
            logger.error("Authentication failed for username: {}. Error: {}", 
                        jwtRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid Credentials", "message", e.getMessage()));
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            logger.debug("Attempting to authenticate user: {}", username);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            logger.warn("User account is disabled: {}", username);
            throw new Exception("USER DISABLED", e);
        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for user: {}", username);
            throw new Exception("INVALID CREDENTIALS", e);
        }
    }
}

