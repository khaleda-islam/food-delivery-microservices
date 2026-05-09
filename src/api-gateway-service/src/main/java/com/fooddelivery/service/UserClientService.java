/*
 * API Gateway Service - User Client Service
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * 
 * Purpose: Client service to call Food Delivery Service user endpoints.
 * Uses WebClient with Eureka service discovery to communicate with the backend.
 */

package com.fooddelivery.service;

import com.fooddelivery.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client service for User operations.
 * 
 * This service acts as a proxy to the Food Delivery Service user endpoints.
 * Handles customer management, authentication, and profile operations.
 * 
 * Service URL Pattern: http://food-delivery-service/api/users
 */
@Service
public class UserClientService {

    private static final Logger logger = LoggerFactory.getLogger(UserClientService.class);
    private static final String SERVICE_URL = "http://food-delivery-service/api/users";

    @Autowired
    private WebClient webClient;

    /**
     * Get all users
     * 
     * @return Flux of all users
     */
    public Flux<User> getAllUsers() {
        logger.info("Calling Food Delivery Service: GET {}", SERVICE_URL);
        return webClient.get()
                .uri(SERVICE_URL)
                .retrieve()
                .bodyToFlux(User.class)
                .doOnError(error -> logger.error("Error fetching all users: {}", error.getMessage()));
    }

    /**
     * Get a specific user by ID
     * 
     * @param id the user ID
     * @return Mono of User
     */
    public Mono<User> getUserById(String id) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(User.class)
                .doOnError(error -> logger.error("Error fetching user {}: {}", id, error.getMessage()));
    }

    /**
     * Create a new user (registration)
     * 
     * @param user the user to create
     * @return Mono of created User
     */
    public Mono<User> createUser(User user) {
        logger.info("Calling Food Delivery Service: POST {} - {}", SERVICE_URL, user.getEmail());
        return webClient.post()
                .uri(SERVICE_URL)
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class)
                .doOnError(error -> logger.error("Error creating user: {}", error.getMessage()));
    }

    /**
     * Update an existing user
     * 
     * @param id the user ID
     * @param user the updated user data
     * @return Mono of updated User
     */
    public Mono<User> updateUser(String id, User user) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: PUT {}", url);
        return webClient.put()
                .uri(url)
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class)
                .doOnError(error -> logger.error("Error updating user {}: {}", id, error.getMessage()));
    }

    /**
     * Delete a user
     * 
     * @param id the user ID
     * @return Mono of Void
     */
    public Mono<Void> deleteUser(String id) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: DELETE {}", url);
        return webClient.delete()
                .uri(url)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> logger.error("Error deleting user {}: {}", id, error.getMessage()));
    }

    /**
     * Find user by email
     * 
     * @param email the user email
     * @return Mono of User
     */
    public Mono<User> findUserByEmail(String email) {
        String url = SERVICE_URL + "/email/" + email;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(User.class)
                .doOnError(error -> logger.error("Error finding user by email {}: {}", email, error.getMessage()));
    }

    /**
     * Get users by city
     * 
     * @param city the city name
     * @return Flux of users in that city
     */
    public Flux<User> getUsersByCity(String city) {
        String url = SERVICE_URL + "/city/" + city;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(User.class)
                .doOnError(error -> logger.error("Error fetching users by city {}: {}", city, error.getMessage()));
    }

    /**
     * Get active users only
     * 
     * @return Flux of active users
     */
    public Flux<User> getActiveUsers() {
        String url = SERVICE_URL + "/active";
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(User.class)
                .doOnError(error -> logger.error("Error fetching active users: {}", error.getMessage()));
    }
}
