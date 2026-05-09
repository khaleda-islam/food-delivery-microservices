/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: REST controller for Restaurant entity with reactive endpoints
 */
package com.fooddelivery.controller;

import com.fooddelivery.model.Restaurant;
import com.fooddelivery.service.RestaurantService;
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
 * REST controller for managing restaurants.
 * Provides reactive API endpoints for restaurant operations.
 */
@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    private RestaurantService service;

    /**
     * Get all restaurants
     * GET /api/restaurants
     * @return Flux of all restaurants
     */
    @GetMapping
    public Flux<Restaurant> getAllRestaurants() {
        logger.info("REST request to get all restaurants");
        return service.findAll();
    }

    /**
     * Get restaurant by ID
     * GET /api/restaurants/{id}
     * @param id the restaurant ID
     * @return Mono of Restaurant with 200 OK or 404 Not Found
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Restaurant>> getRestaurant(@PathVariable String id) {
        logger.info("REST request to get restaurant: {}", id);
        return service.findById(id)
                .map(restaurant -> ResponseEntity.ok(restaurant))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Create a new restaurant
     * POST /api/restaurants
     * @param restaurant the restaurant to create
     * @return Mono of created Restaurant with 201 Created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Restaurant> createRestaurant(@Valid @RequestBody Restaurant restaurant) {
        logger.info("REST request to create restaurant: {}", restaurant.getName());
        restaurant.setId(null); // Ensure new restaurant
        return service.save(restaurant);
    }

    /**
     * Update an existing restaurant
     * PUT /api/restaurants/{id}
     * @param id the restaurant ID
     * @param restaurant the updated restaurant data
     * @return Mono of updated Restaurant with 200 OK or 404 Not Found
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Restaurant>> updateRestaurant(
            @PathVariable String id,
            @Valid @RequestBody Restaurant restaurant) {
        logger.info("REST request to update restaurant: {}", id);
        restaurant.setId(id);
        return service.findById(id)
                .flatMap(existing -> service.save(restaurant))
                .map(updated -> ResponseEntity.ok(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete a restaurant
     * DELETE /api/restaurants/{id}
     * @param id the restaurant ID
     * @return Mono with 204 No Content or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRestaurant(@PathVariable String id) {
        logger.info("REST request to delete restaurant: {}", id);
        return service.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Search restaurants by cuisine type
     * GET /api/restaurants/cuisine/{cuisineType}
     * @param cuisineType the cuisine type
     * @return Flux of restaurants
     */
    @GetMapping("/cuisine/{cuisineType}")
    public Flux<Restaurant> getRestaurantsByCuisine(@PathVariable String cuisineType) {
        logger.info("REST request to get restaurants by cuisine: {}", cuisineType);
        return service.findByCuisineType(cuisineType);
    }

    /**
     * Search restaurants by city
     * GET /api/restaurants/city/{city}
     * @param city the city name
     * @return Flux of restaurants
     */
    @GetMapping("/city/{city}")
    public Flux<Restaurant> getRestaurantsByCity(@PathVariable String city) {
        logger.info("REST request to get restaurants in city: {}", city);
        return service.findByCity(city);
    }

    /**
     * Get restaurants with minimum rating
     * GET /api/restaurants/rating/{minRating}
     * @param minRating minimum rating
     * @return Flux of restaurants
     */
    @GetMapping("/rating/{minRating}")
    public Flux<Restaurant> getRestaurantsByMinRating(@PathVariable Double minRating) {
        logger.info("REST request to get restaurants with rating >= {}", minRating);
        return service.findByMinimumRating(minRating);
    }

    /**
     * Get active restaurants only
     * GET /api/restaurants/active
     * @return Flux of active restaurants
     */
    @GetMapping("/active")
    public Flux<Restaurant> getActiveRestaurants() {
        logger.info("REST request to get active restaurants");
        return service.findActiveRestaurants();
    }

    /**
     * Search restaurants by name
     * GET /api/restaurants/search?name={name}
     * @param name search term
     * @return Flux of restaurants
     */
    @GetMapping("/search")
    public Flux<Restaurant> searchRestaurants(@RequestParam String name) {
        logger.info("REST request to search restaurants by name: {}", name);
        return service.searchByName(name);
    }

    /**
     * Get top-rated restaurants in a city
     * GET /api/restaurants/top-rated?city={city}&minRating={rating}
     * @param city the city name
     * @param minRating minimum rating
     * @return Flux of top-rated restaurants
     */
    @GetMapping("/top-rated")
    public Flux<Restaurant> getTopRatedRestaurants(
            @RequestParam String city,
            @RequestParam(defaultValue = "4.0") Double minRating) {
        logger.info("REST request to get top-rated restaurants in city: {}", city);
        return service.findTopRatedRestaurantsInCity(city, minRating);
    }

    /**
     * Activate a restaurant
     * PATCH /api/restaurants/{id}/activate
     * @param id the restaurant ID
     * @return Mono of activated Restaurant
     */
    @PatchMapping("/{id}/activate")
    public Mono<ResponseEntity<Restaurant>> activateRestaurant(@PathVariable String id) {
        logger.info("REST request to activate restaurant: {}", id);
        return service.activateRestaurant(id)
                .map(restaurant -> ResponseEntity.ok(restaurant))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Deactivate a restaurant
     * PATCH /api/restaurants/{id}/deactivate
     * @param id the restaurant ID
     * @return Mono of deactivated Restaurant
     */
    @PatchMapping("/{id}/deactivate")
    public Mono<ResponseEntity<Restaurant>> deactivateRestaurant(@PathVariable String id) {
        logger.info("REST request to deactivate restaurant: {}", id);
        return service.deactivateRestaurant(id)
                .map(restaurant -> ResponseEntity.ok(restaurant))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
