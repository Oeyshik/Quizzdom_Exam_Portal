package com.exam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExamserverApplication implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(ExamserverApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Exam Server Application...");
		try {
			SpringApplication.run(ExamserverApplication.class, args);
			logger.info("Exam Server Application started successfully");
		} catch (Exception e) {
			logger.fatal("Failed to start Exam Server Application. Error: {}", e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Application initialization started");
		logger.debug("Command line arguments count: {}", args.length);

		// User user = new User();

		// user.setFirstName("Oeyshik");
		// user.setLastName("Das");
		// user.setUsername("Oeyshik007");
		// user.setPassword("password");
		// user.setEmail("odas@gmail.com");
		// user.setPhone("9563241876");
		// user.setProfile("default.png");

		// Role role1 = new Role();
		// role1.setRoleId(18L);
		// role1.setRoleName("ADMIN");

		// Set<UserRole> userRoleSet = new HashSet<>();
		// UserRole userRole = new UserRole();
		// userRole.setRole(role1);
		// userRole.setUser(user);

		// userRoleSet.add(userRole);

		// User user1 = this.userService.createUser(user, userRoleSet);
		// System.out.println(user1.getUsername());

		logger.info("Application initialization completed");
	}

}
