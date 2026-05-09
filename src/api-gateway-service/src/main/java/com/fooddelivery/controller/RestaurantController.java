/*
 * API Gateway Service - Restaurant Controller
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * 
 * Purpose: Public REST API controller for restaurant operations.
 * Exposes endpoints to React frontend, proxies requests to Food Delivery Service.
 */

package com.fooddelivery.controller;

import com.fooddelivery.model.Restaurant;
import com.fooddelivery.service.RestaurantClientService;
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
 * Public-facing REST controller for Restaurant operations.
 * 
 * This controller provides the API Gateway's public interface for restaurant data.
 * All requests are proxied to the Food Delivery Service through the client service.
 * 
 * CORS Configuration:
 * - Allows requests from React dev server (http://localhost:3000)
 * - Configured for development; restrict in production
 * 
 * Base URL: /api/restaurants
 */
@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "http://localhost:3000")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    private RestaurantClientService restaurantClientService;

    /**
     * Get all restaurants
     * 
     * Endpoint: GET /api/restaurants
     * React Usage: Display restaurant listing page
     * 
     * @return Flux of all restaurants
     */
    @GetMapping
    public Flux<Restaurant> getAllRestaurants() {
        logger.info("API Gateway: GET /api/restaurants");
        return restaurantClientService.getAllRestaurants();
    }

    /**
     * Get restaurant by ID
     * 
     * Endpoint: GET /api/restaurants/{id}
     * React Usage: Display restaurant details and menu
     * 
     * @param id the restaurant ID
     * @return Mono of Restaurant with 200 OK or 404 Not Found
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Restaurant>> getRestaurant(@PathVariable String id) {
        logger.info("API Gateway: GET /api/restaurants/{}", id);
        return restaurantClientService.getRestaurantById(id)
                .map(restaurant -> ResponseEntity.ok(restaurant))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Create a new restaurant
     * 
     * Endpoint: POST /api/restaurants
     * React Usage: Admin feature to add new restaurants
     * 
     * @param restaurant the restaurant to create
     * @return Mono of created Restaurant with 201 Created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Restaurant> createRestaurant(@Valid @RequestBody Restaurant restaurant) {
        logger.info("API Gateway: POST /api/restaurants - {}", restaurant.getName());
        return restaurantClientService.createRestaurant(restaurant);
    }

    /**
     * Update an existing restaurant
     * 
     * Endpoint: PUT /api/restaurants/{id}
     * React Usage: Admin feature to update restaurant info
     * 
     * @param id the restaurant ID
     * @param restaurant the updated restaurant data
     * @return Mono of updated Restaurant with 200 OK or 404 Not Found
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Restaurant>> updateRestaurant(
            @PathVariable String id,
            @Valid @RequestBody Restaurant restaurant) {
        logger.info("API Gateway: PUT /api/restaurants/{}", id);
        return restaurantClientService.updateRestaurant(id, restaurant)
                .map(updated -> ResponseEntity.ok(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete a restaurant
     * 
     * Endpoint: DELETE /api/restaurants/{id}
     * React Usage: Admin feature to remove restaurants
     * 
     * @param id the restaurant ID
     * @return Mono with 204 No Content or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRestaurant(@PathVariable String id) {
        logger.info("API Gateway: DELETE /api/restaurants/{}", id);
        return restaurantClientService.deleteRestaurant(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Search restaurants by cuisine type
     * 
     * Endpoint: GET /api/restaurants/cuisine/{cuisineType}
     * React Usage: Filter restaurants by cuisine (Italian, Chinese, etc.)
     * 
     * @param cuisineType the cuisine type
     * @return Flux of restaurants
     */
    @GetMapping("/cuisine/{cuisineType}")
    public Flux<Restaurant> getRestaurantsByCuisine(@PathVariable String cuisineType) {
        logger.info("API Gateway: GET /api/restaurants/cuisine/{}", cuisineType);
        return restaurantClientService.getRestaurantsByCuisine(cuisineType);
    }

    /**
     * Search restaurants by city
     * 
     * Endpoint: GET /api/restaurants/city/{city}
     * React Usage: Find restaurants in user's city
     * 
     * @param city the city name
     * @return Flux of restaurants
     */
    @GetMapping("/city/{city}")
    public Flux<Restaurant> getRestaurantsByCity(@PathVariable String city) {
        logger.info("API Gateway: GET /api/restaurants/city/{}", city);
        return restaurantClientService.getRestaurantsByCity(city);
    }

    /**
     * Get highly rated restaurants
     * 
     * Endpoint: GET /api/restaurants/rating/{rating}
     * React Usage: Show top-rated restaurants (e.g., 4.0+)
     * 
     * @param rating the minimum rating threshold
     * @return Flux of restaurants with rating >= threshold
     */
    @GetMapping("/rating/{rating}")
    public Flux<Restaurant> getRestaurantsByRating(@PathVariable Double rating) {
        logger.info("API Gateway: GET /api/restaurants/rating/{}", rating);
        return restaurantClientService.getRestaurantsByRating(rating);
    }

    /**
     * Get active restaurants only
     * 
     * Endpoint: GET /api/restaurants/active
     * React Usage: Display only restaurants currently accepting orders
     * 
     * @return Flux of active restaurants
     */
    @GetMapping("/active")
    public Flux<Restaurant> getActiveRestaurants() {
        logger.info("API Gateway: GET /api/restaurants/active");
        return restaurantClientService.getActiveRestaurants();
    }
}
