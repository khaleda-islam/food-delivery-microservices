/*
 * API Gateway Service - Food Client Service
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * 
 * Purpose: Client service to call Food Delivery Service food/menu endpoints.
 * Uses WebClient with Eureka service discovery to communicate with the backend.
 */

package com.fooddelivery.service;

import com.fooddelivery.model.Food;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client service for Food operations.
 * 
 * This service acts as a proxy to the Food Delivery Service food endpoints.
 * Handles menu browsing, food item operations, and filtering.
 * 
 * Service URL Pattern: http://food-delivery-service/api/foods
 */
@Service
public class FoodClientService {

    private static final Logger logger = LoggerFactory.getLogger(FoodClientService.class);
    private static final String SERVICE_URL = "http://food-delivery-service/api/foods";

    @Autowired
    private WebClient webClient;

    /**
     * Get all food items
     * 
     * @return Flux of all food items
     */
    public Flux<Food> getAllFoods() {
        logger.info("Calling Food Delivery Service: GET {}", SERVICE_URL);
        return webClient.get()
                .uri(SERVICE_URL)
                .retrieve()
                .bodyToFlux(Food.class)
                .doOnError(error -> logger.error("Error fetching all foods: {}", error.getMessage()));
    }

    /**
     * Get a specific food item by ID
     * 
     * @param id the food ID
     * @return Mono of Food
     */
    public Mono<Food> getFoodById(String id) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Food.class)
                .doOnError(error -> logger.error("Error fetching food {}: {}", id, error.getMessage()));
    }

    /**
     * Create a new food item
     * 
     * @param food the food to create
     * @return Mono of created Food
     */
    public Mono<Food> createFood(Food food) {
        logger.info("Calling Food Delivery Service: POST {} - {}", SERVICE_URL, food.getName());
        return webClient.post()
                .uri(SERVICE_URL)
                .bodyValue(food)
                .retrieve()
                .bodyToMono(Food.class)
                .doOnError(error -> logger.error("Error creating food: {}", error.getMessage()));
    }

    /**
     * Update an existing food item
     * 
     * @param id the food ID
     * @param food the updated food data
     * @return Mono of updated Food
     */
    public Mono<Food> updateFood(String id, Food food) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: PUT {}", url);
        return webClient.put()
                .uri(url)
                .bodyValue(food)
                .retrieve()
                .bodyToMono(Food.class)
                .doOnError(error -> logger.error("Error updating food {}: {}", id, error.getMessage()));
    }

    /**
     * Delete a food item
     * 
     * @param id the food ID
     * @return Mono of Void
     */
    public Mono<Void> deleteFood(String id) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: DELETE {}", url);
        return webClient.delete()
                .uri(url)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> logger.error("Error deleting food {}: {}", id, error.getMessage()));
    }

    /**
     * Get menu for a specific restaurant
     * 
     * @param restaurantId the restaurant ID
     * @return Flux of food items for that restaurant
     */
    public Flux<Food> getRestaurantMenu(String restaurantId) {
        String url = SERVICE_URL + "/restaurant/" + restaurantId;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Food.class)
                .doOnError(error -> logger.error("Error fetching menu for restaurant {}: {}", restaurantId, error.getMessage()));
    }

    /**
     * Get available menu items for a restaurant
     * 
     * @param restaurantId the restaurant ID
     * @return Flux of available food items
     */
    public Flux<Food> getAvailableMenuItems(String restaurantId) {
        String url = SERVICE_URL + "/restaurant/" + restaurantId + "/available";
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Food.class)
                .doOnError(error -> logger.error("Error fetching available menu for restaurant {}: {}", restaurantId, error.getMessage()));
    }

    /**
     * Get food items by category
     * 
     * @param category the food category
     * @return Flux of food items in that category
     */
    public Flux<Food> getFoodsByCategory(String category) {
        String url = SERVICE_URL + "/category/" + category;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Food.class)
                .doOnError(error -> logger.error("Error fetching foods by category {}: {}", category, error.getMessage()));
    }

    /**
     * Get vegetarian food items
     * 
     * @return Flux of vegetarian food items
     */
    public Flux<Food> getVegetarianFoods() {
        String url = SERVICE_URL + "/vegetarian";
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Food.class)
                .doOnError(error -> logger.error("Error fetching vegetarian foods: {}", error.getMessage()));
    }

    /**
     * Get vegan food items
     * 
     * @return Flux of vegan food items
     */
    public Flux<Food> getVeganFoods() {
        String url = SERVICE_URL + "/vegan";
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Food.class)
                .doOnError(error -> logger.error("Error fetching vegan foods: {}", error.getMessage()));
    }

    /**
     * Search food items by name
     * 
     * @param searchTerm the search term
     * @return Flux of matching food items
     */
    public Flux<Food> searchFoods(String searchTerm) {
        String url = SERVICE_URL + "/search?q=" + searchTerm;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Food.class)
                .doOnError(error -> logger.error("Error searching foods with term '{}': {}", searchTerm, error.getMessage()));
    }
}
