/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Reactive repository for User entity with custom query methods
 */
package com.fooddelivery.repository;

import com.fooddelivery.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive repository for User entity.
 * Provides CRUD operations and custom query methods using reactive types.
 */
@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    /**
     * Find a user by email address (unique)
     * @param email the user's email address
     * @return Mono of User if found
     */
    Mono<User> findByEmail(String email);

    /**
     * Find a user by email (case-insensitive)
     * @param email the user's email address
     * @return Mono of User if found
     */
    Mono<User> findByEmailIgnoreCase(String email);

    /**
     * Find users by role
     * @param role the user role (CUSTOMER, ADMIN, DELIVERY_PARTNER)
     * @return Flux of users with the specified role
     */
    Flux<User> findByRole(String role);

    /**
     * Find users by city
     * @param city the city name
     * @return Flux of users in the specified city
     */
    Flux<User> findByCity(String city);

    /**
     * Find all active users
     * @param isActive true to find active users, false for inactive
     * @return Flux of users matching the active status
     */
    Flux<User> findByIsActive(Boolean isActive);

    /**
     * Find users by name containing a search term (case-insensitive)
     * @param name search term for user name
     * @return Flux of users with names containing the search term
     */
    Flux<User> findByNameContainingIgnoreCase(String name);

    /**
     * Find users by role and active status
     * @param role the user role
     * @param isActive active status
     * @return Flux of users matching both criteria
     */
    Flux<User> findByRoleAndIsActive(String role, Boolean isActive);

    /**
     * Find users by city and role
     * @param city the city name
     * @param role the user role
     * @return Flux of users matching both criteria
     */
    Flux<User> findByCityAndRole(String city, String role);

    /**
     * Find a user by phone number
     * @param phoneNumber the user's phone number
     * @return Mono of User if found
     */
    Mono<User> findByPhoneNumber(String phoneNumber);

    /**
     * Check if a user exists by email
     * @param email the email address to check
     * @return Mono of Boolean indicating existence
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Check if a user exists by phone number
     * @param phoneNumber the phone number to check
     * @return Mono of Boolean indicating existence
     */
    Mono<Boolean> existsByPhoneNumber(String phoneNumber);

    /**
     * Count users by role
     * @param role the user role
     * @return Mono of Long representing the count
     */
    Mono<Long> countByRole(String role);

    /**
     * Count active users
     * @param isActive active status
     * @return Mono of Long representing the count
     */
    Mono<Long> countByIsActive(Boolean isActive);

    /**
     * Find users by email and password (for authentication)
     * @param email the user's email
     * @param password the user's password
     * @return Mono of User if credentials match
     */
    Mono<User> findByEmailAndPassword(String email, String password);
}
