/*
 * API Gateway Service - User Controller
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * 
 * Purpose: Public REST API controller for user/customer operations.
 * Exposes endpoints to React frontend, proxies requests to Food Delivery Service.
 */

package com.fooddelivery.controller;

import com.fooddelivery.model.User;
import com.fooddelivery.service.UserClientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Public-facing REST controller for User operations.
 * 
 * This controller provides the API Gateway's public interface for user management.
 * All requests are proxied to the Food Delivery Service through the client service.
 * 
 * CORS Configuration:
 * - Allows requests from React dev server (http://localhost:3000)
 * 
 * Base URL: /api/users
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserClientService userClientService;

    /**
     * Get all users
     * 
     * Endpoint: GET /api/users
     * React Usage: Admin feature to view all customers
     * 
     * @return Flux of all users
     */
    @GetMapping
    public Flux<User> getAllUsers() {
        logger.info("API Gateway: GET /api/users");
        return userClientService.getAllUsers();
    }

    /**
     * Get user by ID
     * 
     * Endpoint: GET /api/users/{id}
     * React Usage: Display user profile
     * 
     * @param id the user ID
     * @return Mono of User with 200 OK or 404 Not Found
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUser(@PathVariable String id) {
        logger.info("API Gateway: GET /api/users/{}", id);
        return userClientService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Create a new user (registration)
     * 
     * Endpoint: POST /api/users
     * React Usage: User registration form
     * 
     * @param user the user to create
     * @return Mono of created User with 201 Created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> createUser(@Valid @RequestBody User user) {
        logger.info("API Gateway: POST /api/users - {}", user.getEmail());
        return userClientService.createUser(user);
    }

    /**
     * Update an existing user
     * 
     * Endpoint: PUT /api/users/{id}
     * React Usage: Update user profile
     * 
     * @param id the user ID
     * @param user the updated user data
     * @return Mono of updated User with 200 OK or 404 Not Found
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody User user) {
        logger.info("API Gateway: PUT /api/users/{}", id);
        return userClientService.updateUser(id, user)
                .map(updated -> ResponseEntity.ok(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete a user
     * 
     * Endpoint: DELETE /api/users/{id}
     * React Usage: Delete account feature
     * 
     * @param id the user ID
     * @return Mono with 204 No Content or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        logger.info("API Gateway: DELETE /api/users/{}", id);
        return userClientService.deleteUser(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Find user by email
     * 
     * Endpoint: GET /api/users/email/{email}
     * React Usage: Login, email verification
     * 
     * @param email the user email
     * @return Mono of User with 200 OK or 404 Not Found
     */
    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<User>> findUserByEmail(@PathVariable String email) {
        logger.info("API Gateway: GET /api/users/email/{}", email);
        return userClientService.findUserByEmail(email)
                .map(user -> ResponseEntity.ok(user))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Get users by city
     * 
     * Endpoint: GET /api/users/city/{city}
     * React Usage: Admin feature - view users by location
     * 
     * @param city the city name
     * @return Flux of users in that city
     */
    @GetMapping("/city/{city}")
    public Flux<User> getUsersByCity(@PathVariable String city) {
        logger.info("API Gateway: GET /api/users/city/{}", city);
        return userClientService.getUsersByCity(city);
    }

    /**
     * Get active users only
     * 
     * Endpoint: GET /api/users/active
     * React Usage: Admin feature - view active customers
     * 
     * @return Flux of active users
     */
    @GetMapping("/active")
    public Flux<User> getActiveUsers() {
        logger.info("API Gateway: GET /api/users/active");
        return userClientService.getActiveUsers();
    }
}
