/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Service layer for User entity with business logic and error handling
 */
package com.fooddelivery.service;

import com.fooddelivery.model.User;
import com.fooddelivery.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

/**
 * Service class for User entity.
 * Handles user management, authentication, and business logic.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository repository;

    /**
     * Find all users
     * @return Flux of all users
     */
    public Flux<User> findAll() {
        logger.info("Fetching all users");
        return repository.findAll()
                .doOnComplete(() -> logger.info("Successfully fetched all users"))
                .doOnError(error -> logger.error("Error fetching all users", error));
    }

    /**
     * Find user by ID
     * @param id the user ID
     * @return Mono of User if found, empty Mono otherwise
     */
    public Mono<User> findById(String id) {
        logger.info("Fetching user with ID: {}", id);
        return repository.findById(id)
                .doOnSuccess(user -> {
                    if (user != null) {
                        logger.info("Found user: {}", user.getEmail());
                    } else {
                        logger.warn("User not found with ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error fetching user with ID: {}", id, error))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("No user found with ID: {}", id);
                    return Mono.empty();
                }));
    }

    /**
     * Find user by email
     * @param email the user's email
     * @return Mono of User if found
     */
    public Mono<User> findByEmail(String email) {
        logger.info("Fetching user with email: {}", email);
        return repository.findByEmailIgnoreCase(email)
                .doOnSuccess(user -> {
                    if (user != null) {
                        logger.info("Found user: {}", user.getName());
                    } else {
                        logger.warn("User not found with email: {}", email);
                    }
                })
                .doOnError(error -> logger.error("Error fetching user by email: {}", email, error));
    }

    /**
     * Register a new user
     * @param user the user to register
     * @return Mono of registered User
     */
    public Mono<User> registerUser(User user) {
        logger.info("Registering new user: {}", user.getEmail());
        
        // Check if email already exists
        return repository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        logger.warn("Registration failed: Email already exists: {}", user.getEmail());
                        return Mono.error(new RuntimeException("Email already registered: " + user.getEmail()));
                    }
                    
                    // Check if phone number already exists
                    return repository.existsByPhoneNumber(user.getPhoneNumber())
                            .flatMap(phoneExists -> {
                                if (phoneExists) {
                                    logger.warn("Registration failed: Phone number already exists: {}", user.getPhoneNumber());
                                    return Mono.error(new RuntimeException("Phone number already registered: " + user.getPhoneNumber()));
                                }
                                
                                // Set registration metadata
                                user.setCreatedAt(LocalDateTime.now());
                                user.setUpdatedAt(LocalDateTime.now());
                                user.setIsActive(true);
                                if (user.getRole() == null || user.getRole().isEmpty()) {
                                    user.setRole("CUSTOMER");
                                }
                                
                                return repository.save(user);
                            });
                })
                .doOnSuccess(savedUser -> logger.info("Successfully registered user: {} with ID: {}", 
                        savedUser.getEmail(), savedUser.getId()))
                .doOnError(error -> logger.error("Error registering user: {}", user.getEmail(), error))
                .onErrorResume(error -> {
                    logger.error("Failed to register user", error);
                    return Mono.error(error);
                });
    }

    /**
     * Save or update user (without duplicate checks)
     * Used for guest checkout where user might already exist
     * @param user the user to save
     * @return Mono of saved User
     */
    public Mono<User> save(User user) {
        logger.info("Saving user: {}", user.getEmail());
        user.setUpdatedAt(LocalDateTime.now());
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        return repository.save(user)
                .doOnSuccess(saved -> logger.info("User saved successfully: {}", saved.getId()))
                .doOnError(error -> logger.error("Error saving user", error));
    }

    /**
     * Update user information
     * @param user the user with updated information
     * @return Mono of updated User
     */
    public Mono<User> updateUser(User user) {
        logger.info("Updating user with ID: {}", user.getId());
        
        return repository.findById(user.getId())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with ID: " + user.getId())))
                .flatMap(existingUser -> {
                    user.setUpdatedAt(LocalDateTime.now());
                    user.setCreatedAt(existingUser.getCreatedAt()); // Preserve creation date
                    return repository.save(user);
                })
                .doOnSuccess(updated -> logger.info("Successfully updated user: {}", updated.getEmail()))
                .doOnError(error -> logger.error("Error updating user with ID: {}", user.getId(), error));
    }

    /**
     * Delete user by ID
     * @param id the user ID
     * @return Mono of Void when deletion is complete
     */
    public Mono<Void> deleteById(String id) {
        logger.info("Deleting user with ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("Cannot delete: User not found with ID: {}", id);
                    return Mono.error(new RuntimeException("User not found with ID: " + id));
                }))
                .flatMap(user -> {
                    logger.info("Deleting user: {}", user.getEmail());
                    return repository.deleteById(id);
                })
                .doOnSuccess(unused -> logger.info("Successfully deleted user with ID: {}", id))
                .doOnError(error -> logger.error("Error deleting user with ID: {}", id, error));
    }

    /**
     * Authenticate user with email and password
     * @param email the user's email
     * @param password the user's password
     * @return Mono of User if authentication successful
     */
    public Mono<User> authenticate(String email, String password) {
        logger.info("Attempting authentication for email: {}", email);
        return repository.findByEmailAndPassword(email, password)
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("Authentication failed for email: {}", email);
                    return Mono.error(new RuntimeException("Invalid email or password"));
                }))
                .flatMap(user -> {
                    if (!user.getIsActive()) {
                        logger.warn("Authentication failed: User account is inactive: {}", email);
                        return Mono.error(new RuntimeException("User account is inactive"));
                    }
                    logger.info("Authentication successful for: {}", email);
                    return Mono.just(user);
                })
                .doOnError(error -> logger.error("Authentication error for email: {}", email, error));
    }

    /**
     * Find users by role
     * @param role the user role
     * @return Flux of users with the specified role
     */
    public Flux<User> findByRole(String role) {
        logger.info("Fetching users with role: {}", role);
        return repository.findByRole(role)
                .doOnComplete(() -> logger.info("Successfully fetched users with role: {}", role))
                .doOnError(error -> logger.error("Error fetching users by role: {}", role, error));
    }

    /**
     * Find active users by role
     * @param role the user role
     * @return Flux of active users with the specified role
     */
    public Flux<User> findActiveUsersByRole(String role) {
        logger.info("Fetching active users with role: {}", role);
        return repository.findByRoleAndIsActive(role, true)
                .doOnComplete(() -> logger.info("Successfully fetched active users with role: {}", role))
                .doOnError(error -> logger.error("Error fetching active users by role", error));
    }

    /**
     * Find users by city
     * @param city the city name
     * @return Flux of users in the city
     */
    public Flux<User> findByCity(String city) {
        logger.info("Fetching users in city: {}", city);
        return repository.findByCity(city)
                .doOnComplete(() -> logger.info("Successfully fetched users in city: {}", city))
                .doOnError(error -> logger.error("Error fetching users by city", error));
    }

    /**
     * Search users by name
     * @param name search term for user name
     * @return Flux of users with names containing the search term
     */
    public Flux<User> searchByName(String name) {
        logger.info("Searching users by name: {}", name);
        return repository.findByNameContainingIgnoreCase(name)
                .doOnComplete(() -> logger.info("Successfully searched users by name: {}", name))
                .doOnError(error -> logger.error("Error searching users by name", error));
    }

    /**
     * Activate user account
     * @param id the user ID
     * @return Mono of updated User
     */
    public Mono<User> activateUser(String id) {
        logger.info("Activating user account: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with ID: " + id)))
                .flatMap(user -> {
                    user.setIsActive(true);
                    user.setUpdatedAt(LocalDateTime.now());
                    return repository.save(user);
                })
                .doOnSuccess(user -> logger.info("Successfully activated user: {}", user.getEmail()))
                .doOnError(error -> logger.error("Error activating user", error));
    }

    /**
     * Deactivate user account
     * @param id the user ID
     * @return Mono of updated User
     */
    public Mono<User> deactivateUser(String id) {
        logger.info("Deactivating user account: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with ID: " + id)))
                .flatMap(user -> {
                    user.setIsActive(false);
                    user.setUpdatedAt(LocalDateTime.now());
                    return repository.save(user);
                })
                .doOnSuccess(user -> logger.info("Successfully deactivated user: {}", user.getEmail()))
                .doOnError(error -> logger.error("Error deactivating user", error));
    }

    /**
     * Update user role
     * @param id the user ID
     * @param newRole the new role to assign
     * @return Mono of updated User
     */
    public Mono<User> updateUserRole(String id, String newRole) {
        logger.info("Updating role for user: {} to: {}", id, newRole);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with ID: " + id)))
                .flatMap(user -> {
                    user.setRole(newRole);
                    user.setUpdatedAt(LocalDateTime.now());
                    return repository.save(user);
                })
                .doOnSuccess(user -> logger.info("Successfully updated role for user: {}", user.getEmail()))
                .doOnError(error -> logger.error("Error updating user role", error));
    }

    /**
     * Count users by role
     * @param role the user role
     * @return Mono of Long representing the count
     */
    public Mono<Long> countByRole(String role) {
        logger.info("Counting users with role: {}", role);
        return repository.countByRole(role)
                .doOnSuccess(count -> logger.info("Found {} users with role: {}", count, role))
                .doOnError(error -> logger.error("Error counting users by role", error));
    }

    /**
     * Count active users
     * @return Mono of Long representing the count
     */
    public Mono<Long> countActiveUsers() {
        logger.info("Counting active users");
        return repository.countByIsActive(true)
                .doOnSuccess(count -> logger.info("Found {} active users", count))
                .doOnError(error -> logger.error("Error counting active users", error));
    }

    /**
     * Check if email is already registered
     * @param email the email to check
     * @return Mono of Boolean indicating if email exists
     */
    public Mono<Boolean> isEmailRegistered(String email) {
        logger.info("Checking if email is registered: {}", email);
        return repository.existsByEmail(email)
                .doOnSuccess(exists -> logger.info("Email {} registration status: {}", email, exists))
                .doOnError(error -> logger.error("Error checking email registration", error));
    }
}
