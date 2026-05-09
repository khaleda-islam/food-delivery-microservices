/*
 * API Gateway Service - Food Controller
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * 
 * Purpose: Public REST API controller for food/menu operations.
 * Exposes endpoints to React frontend, proxies requests to Food Delivery Service.
 */

package com.fooddelivery.controller;

import com.fooddelivery.model.Food;
import com.fooddelivery.service.FoodClientService;
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
 * Public-facing REST controller for Food operations.
 * 
 * This controller provides the API Gateway's public interface for menu browsing.
 * All requests are proxied to the Food Delivery Service through the client service.
 * 
 * CORS Configuration:
 * - Allows requests from React dev server (http://localhost:3000)
 * 
 * Base URL: /api/foods
 */
@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "http://localhost:3000")
public class FoodController {

    private static final Logger logger = LoggerFactory.getLogger(FoodController.class);

    @Autowired
    private FoodClientService foodClientService;

    /**
     * Get all food items
     * 
     * Endpoint: GET /api/foods
     * React Usage: Display all available food items
     * 
     * @return Flux of all food items
     */
    @GetMapping
    public Flux<Food> getAllFoods() {
        logger.info("API Gateway: GET /api/foods");
        return foodClientService.getAllFoods();
    }

    /**
     * Get food item by ID
     * 
     * Endpoint: GET /api/foods/{id}
     * React Usage: Display food item details
     * 
     * @param id the food ID
     * @return Mono of Food with 200 OK or 404 Not Found
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Food>> getFood(@PathVariable String id) {
        logger.info("API Gateway: GET /api/foods/{}", id);
        return foodClientService.getFoodById(id)
                .map(food -> ResponseEntity.ok(food))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Create a new food item
     * 
     * Endpoint: POST /api/foods
     * React Usage: Admin feature to add menu items
     * 
     * @param food the food to create
     * @return Mono of created Food with 201 Created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Food> createFood(@Valid @RequestBody Food food) {
        logger.info("API Gateway: POST /api/foods - {}", food.getName());
        return foodClientService.createFood(food);
    }

    /**
     * Update an existing food item
     * 
     * Endpoint: PUT /api/foods/{id}
     * React Usage: Admin feature to update menu items
     * 
     * @param id the food ID
     * @param food the updated food data
     * @return Mono of updated Food with 200 OK or 404 Not Found
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Food>> updateFood(
            @PathVariable String id,
            @Valid @RequestBody Food food) {
        logger.info("API Gateway: PUT /api/foods/{}", id);
        return foodClientService.updateFood(id, food)
                .map(updated -> ResponseEntity.ok(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete a food item
     * 
     * Endpoint: DELETE /api/foods/{id}
     * React Usage: Admin feature to remove menu items
     * 
     * @param id the food ID
     * @return Mono with 204 No Content or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteFood(@PathVariable String id) {
        logger.info("API Gateway: DELETE /api/foods/{}", id);
        return foodClientService.deleteFood(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Get menu for a specific restaurant
     * 
     * Endpoint: GET /api/foods/restaurant/{restaurantId}
     * React Usage: Display restaurant menu page
     * 
     * @param restaurantId the restaurant ID
     * @return Flux of food items for that restaurant
     */
    @GetMapping("/restaurant/{restaurantId}")
    public Flux<Food> getRestaurantMenu(@PathVariable String restaurantId) {
        logger.info("API Gateway: GET /api/foods/restaurant/{}", restaurantId);
        return foodClientService.getRestaurantMenu(restaurantId);
    }

    /**
     * Get available menu items for a restaurant
     * 
     * Endpoint: GET /api/foods/restaurant/{restaurantId}/available
     * React Usage: Show only items currently available for order
     * 
     * @param restaurantId the restaurant ID
     * @return Flux of available food items
     */
    @GetMapping("/restaurant/{restaurantId}/available")
    public Flux<Food> getAvailableMenuItems(@PathVariable String restaurantId) {
        logger.info("API Gateway: GET /api/foods/restaurant/{}/available", restaurantId);
        return foodClientService.getAvailableMenuItems(restaurantId);
    }

    /**
     * Get food items by category
     * 
     * Endpoint: GET /api/foods/category/{category}
     * React Usage: Filter menu by category (Appetizer, Main Course, etc.)
     * 
     * @param category the food category
     * @return Flux of food items in that category
     */
    @GetMapping("/category/{category}")
    public Flux<Food> getFoodsByCategory(@PathVariable String category) {
        logger.info("API Gateway: GET /api/foods/category/{}", category);
        return foodClientService.getFoodsByCategory(category);
    }

    /**
     * Get vegetarian food items
     * 
     * Endpoint: GET /api/foods/vegetarian
     * React Usage: Filter for vegetarian options
     * 
     * @return Flux of vegetarian food items
     */
    @GetMapping("/vegetarian")
    public Flux<Food> getVegetarianFoods() {
        logger.info("API Gateway: GET /api/foods/vegetarian");
        return foodClientService.getVegetarianFoods();
    }

    /**
     * Get vegan food items
     * 
     * Endpoint: GET /api/foods/vegan
     * React Usage: Filter for vegan options
     * 
     * @return Flux of vegan food items
     */
    @GetMapping("/vegan")
    public Flux<Food> getVeganFoods() {
        logger.info("API Gateway: GET /api/foods/vegan");
        return foodClientService.getVeganFoods();
    }

    /**
     * Search food items by name
     * 
     * Endpoint: GET /api/foods/search?q={searchTerm}
     * React Usage: Search functionality for finding specific dishes
     * 
     * @param searchTerm the search term
     * @return Flux of matching food items
     */
    @GetMapping("/search")
    public Flux<Food> searchFoods(@RequestParam String q) {
        logger.info("API Gateway: GET /api/foods/search?q={}", q);
        return foodClientService.searchFoods(q);
    }
}
