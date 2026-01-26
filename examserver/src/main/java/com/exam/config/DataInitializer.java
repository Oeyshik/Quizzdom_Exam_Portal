package com.exam.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.exam.service.RoleService;

@Component
@Order(1) // Run this first before other CommandLineRunner beans
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(DataInitializer.class);

    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");
        
        try {
            // Initialize default roles
            roleService.initRoles();
            logger.info("Data initialization completed successfully");
        } catch (Exception e) {
            logger.error("Error during data initialization: {}", e.getMessage(), e);
            // Don't throw exception - allow application to continue
        }
    }
}

