/*
 * API Gateway Service - Restaurant Client Service
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * 
 * Purpose: Client service to call Food Delivery Service restaurant endpoints.
 * Uses WebClient with Eureka service discovery to communicate with the backend.
 */

package com.fooddelivery.service;

import com.fooddelivery.model.Restaurant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client service for Restaurant operations.
 * 
 * This service acts as a proxy to the Food Delivery Service restaurant endpoints.
 * It uses WebClient with @LoadBalanced configuration to:
 * - Discover service instances through Eureka
 * - Load balance requests across multiple instances
 * - Make reactive HTTP calls
 * 
 * Service URL Pattern: http://food-delivery-service/api/restaurants
 */
@Service
public class RestaurantClientService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantClientService.class);
    private static final String SERVICE_URL = "http://food-delivery-service/api/restaurants";

    @Autowired
    private WebClient webClient;

    /**
     * Get all restaurants from Food Delivery Service
     * 
     * @return Flux of all restaurants
     */
    public Flux<Restaurant> getAllRestaurants() {
        logger.info("Calling Food Delivery Service: GET {}", SERVICE_URL);
        return webClient.get()
                .uri(SERVICE_URL)
                .retrieve()
                .bodyToFlux(Restaurant.class)
                .doOnError(error -> logger.error("Error fetching all restaurants: {}", error.getMessage()));
    }

    /**
     * Get a specific restaurant by ID
     * 
     * @param id the restaurant ID
     * @return Mono of Restaurant
     */
    public Mono<Restaurant> getRestaurantById(String id) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Restaurant.class)
                .doOnError(error -> logger.error("Error fetching restaurant {}: {}", id, error.getMessage()));
    }

    /**
     * Create a new restaurant
     * 
     * @param restaurant the restaurant to create
     * @return Mono of created Restaurant
     */
    public Mono<Restaurant> createRestaurant(Restaurant restaurant) {
        logger.info("Calling Food Delivery Service: POST {} - {}", SERVICE_URL, restaurant.getName());
        return webClient.post()
                .uri(SERVICE_URL)
                .bodyValue(restaurant)
                .retrieve()
                .bodyToMono(Restaurant.class)
                .doOnError(error -> logger.error("Error creating restaurant: {}", error.getMessage()));
    }

    /**
     * Update an existing restaurant
     * 
     * @param id the restaurant ID
     * @param restaurant the updated restaurant data
     * @return Mono of updated Restaurant
     */
    public Mono<Restaurant> updateRestaurant(String id, Restaurant restaurant) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: PUT {}", url);
        return webClient.put()
                .uri(url)
                .bodyValue(restaurant)
                .retrieve()
                .bodyToMono(Restaurant.class)
                .doOnError(error -> logger.error("Error updating restaurant {}: {}", id, error.getMessage()));
    }

    /**
     * Delete a restaurant
     * 
     * @param id the restaurant ID
     * @return Mono of Void
     */
    public Mono<Void> deleteRestaurant(String id) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: DELETE {}", url);
        return webClient.delete()
                .uri(url)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> logger.error("Error deleting restaurant {}: {}", id, error.getMessage()));
    }

    /**
     * Search restaurants by cuisine type
     * 
     * @param cuisineType the cuisine type
     * @return Flux of restaurants
     */
    public Flux<Restaurant> getRestaurantsByCuisine(String cuisineType) {
        String url = SERVICE_URL + "/cuisine/" + cuisineType;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Restaurant.class)
                .doOnError(error -> logger.error("Error fetching restaurants by cuisine {}: {}", cuisineType, error.getMessage()));
    }

    /**
     * Search restaurants by city
     * 
     * @param city the city name
     * @return Flux of restaurants
     */
    public Flux<Restaurant> getRestaurantsByCity(String city) {
        String url = SERVICE_URL + "/city/" + city;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Restaurant.class)
                .doOnError(error -> logger.error("Error fetching restaurants by city {}: {}", city, error.getMessage()));
    }

    /**
     * Get highly rated restaurants (rating >= threshold)
     * 
     * @param rating the minimum rating
     * @return Flux of restaurants
     */
    public Flux<Restaurant> getRestaurantsByRating(Double rating) {
        String url = SERVICE_URL + "/rating/" + rating;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Restaurant.class)
                .doOnError(error -> logger.error("Error fetching restaurants by rating >= {}: {}", rating, error.getMessage()));
    }

    /**
     * Get active restaurants only
     * 
     * @return Flux of active restaurants
     */
    public Flux<Restaurant> getActiveRestaurants() {
        String url = SERVICE_URL + "/active";
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Restaurant.class)
                .doOnError(error -> logger.error("Error fetching active restaurants: {}", error.getMessage()));
    }
}
