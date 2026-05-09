/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: REST controller for User entity with reactive endpoints
 */
package com.fooddelivery.controller;

import com.fooddelivery.model.User;
import com.fooddelivery.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * REST controller for managing users.
 * Provides reactive API endpoints for user operations including authentication.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService service;

    /**
     * Get all users
     * GET /api/users
     * @return Flux of all users
     */
    @GetMapping
    public Flux<User> getAllUsers() {
        logger.info("REST request to get all users");
        return service.findAll();
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     * @param id the user ID
     * @return Mono of User with 200 OK or 404 Not Found
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUser(@PathVariable String id) {
        logger.info("REST request to get user: {}", id);
        return service.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Get user by email
     * GET /api/users/email/{email}
     * @param email the user's email
     * @return Mono of User with 200 OK or 404 Not Found
     */
    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<User>> getUserByEmail(@PathVariable String email) {
        logger.info("REST request to get user by email: {}", email);
        return service.findByEmail(email)
                .map(user -> ResponseEntity.ok(user))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Register a new user
     * POST /api/users/register
     * @param user the user to register
     * @return Mono of registered User with 201 Created or 400 Bad Request
     */
    @PostMapping("/register")
    public Mono<ResponseEntity<User>> registerUser(@Valid @RequestBody User user) {
        logger.info("REST request to register user: {}", user.getEmail());
        return service.registerUser(user)
                .map(registered -> ResponseEntity.status(HttpStatus.CREATED).body(registered))
                .onErrorResume(e -> {
                    logger.error("Error registering user: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    /**
     * Authenticate user
     * POST /api/users/login
     * @param credentials map containing email and password
     * @return Mono of User with 200 OK or 401 Unauthorized
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<User>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        logger.info("REST request to authenticate user: {}", email);
        
        return service.authenticate(email, password)
                .map(user -> ResponseEntity.ok(user))
                .onErrorResume(e -> {
                    logger.warn("Authentication failed for: {}", email);
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }

    /**
     * Update user information
     * PUT /api/users/{id}
     * @param id the user ID
     * @param user the updated user data
     * @return Mono of updated User with 200 OK or 404 Not Found
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody User user) {
        logger.info("REST request to update user: {}", id);
        user.setId(id);
        return service.updateUser(user)
                .map(updated -> ResponseEntity.ok(updated))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Delete a user
     * DELETE /api/users/{id}
     * @param id the user ID
     * @return Mono with 204 No Content or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        logger.info("REST request to delete user: {}", id);
        return service.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Get users by role
     * GET /api/users/role/{role}
     * @param role the user role
     * @return Flux of users
     */
    @GetMapping("/role/{role}")
    public Flux<User> getUsersByRole(@PathVariable String role) {
        logger.info("REST request to get users by role: {}", role);
        return service.findByRole(role);
    }

    /**
     * Get active users by role
     * GET /api/users/role/{role}/active
     * @param role the user role
     * @return Flux of active users
     */
    @GetMapping("/role/{role}/active")
    public Flux<User> getActiveUsersByRole(@PathVariable String role) {
        logger.info("REST request to get active users by role: {}", role);
        return service.findActiveUsersByRole(role);
    }

    /**
     * Get users by city
     * GET /api/users/city/{city}
     * @param city the city name
     * @return Flux of users
     */
    @GetMapping("/city/{city}")
    public Flux<User> getUsersByCity(@PathVariable String city) {
        logger.info("REST request to get users in city: {}", city);
        return service.findByCity(city);
    }

    /**
     * Search users by name
     * GET /api/users/search?name={name}
     * @param name search term
     * @return Flux of users
     */
    @GetMapping("/search")
    public Flux<User> searchUsers(@RequestParam String name) {
        logger.info("REST request to search users by name: {}", name);
        return service.searchByName(name);
    }

    /**
     * Activate user account
     * PATCH /api/users/{id}/activate
     * @param id the user ID
     * @return Mono of activated User
     */
    @PatchMapping("/{id}/activate")
    public Mono<ResponseEntity<User>> activateUser(@PathVariable String id) {
        logger.info("REST request to activate user: {}", id);
        return service.activateUser(id)
                .map(user -> ResponseEntity.ok(user))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Deactivate user account
     * PATCH /api/users/{id}/deactivate
     * @param id the user ID
     * @return Mono of deactivated User
     */
    @PatchMapping("/{id}/deactivate")
    public Mono<ResponseEntity<User>> deactivateUser(@PathVariable String id) {
        logger.info("REST request to deactivate user: {}", id);
        return service.deactivateUser(id)
                .map(user -> ResponseEntity.ok(user))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Update user role
     * PATCH /api/users/{id}/role
     * @param id the user ID
     * @param roleData map containing the new role
     * @return Mono of updated User
     */
    @PatchMapping("/{id}/role")
    public Mono<ResponseEntity<User>> updateUserRole(
            @PathVariable String id,
            @RequestBody Map<String, String> roleData) {
        String newRole = roleData.get("role");
        logger.info("REST request to update role for user: {} to: {}", id, newRole);
        return service.updateUserRole(id, newRole)
                .map(user -> ResponseEntity.ok(user))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Count users by role
     * GET /api/users/role/{role}/count
     * @param role the user role
     * @return Mono of Long
     */
    @GetMapping("/role/{role}/count")
    public Mono<Long> countByRole(@PathVariable String role) {
        logger.info("REST request to count users by role: {}", role);
        return service.countByRole(role);
    }

    /**
     * Count active users
     * GET /api/users/active/count
     * @return Mono of Long
     */
    @GetMapping("/active/count")
    public Mono<Long> countActiveUsers() {
        logger.info("REST request to count active users");
        return service.countActiveUsers();
    }

    /**
     * Check if email is already registered
     * GET /api/users/check-email?email={email}
     * @param email the email to check
     * @return Mono of Boolean
     */
    @GetMapping("/check-email")
    public Mono<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        logger.info("REST request to check if email is registered: {}", email);
        return service.isEmailRegistered(email)
                .map(exists -> Map.of("exists", exists));
    }
}
