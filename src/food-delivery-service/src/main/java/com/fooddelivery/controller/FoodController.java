/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: REST controller for Food entity with reactive endpoints
 */
package com.fooddelivery.controller;

import com.fooddelivery.model.Food;
import com.fooddelivery.service.FoodService;
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
 * REST controller for managing food items.
 * Provides reactive API endpoints for food item operations.
 */
@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
public class FoodController {

    private static final Logger logger = LoggerFactory.getLogger(FoodController.class);

    @Autowired
    private FoodService service;

    /**
     * Get all food items
     * GET /api/foods
     * @return Flux of all food items
     */
    @GetMapping
    public Flux<Food> getAllFoods() {
        logger.info("REST request to get all food items");
        return service.findAll();
    }

    /**
     * Get food item by ID
     * GET /api/foods/{id}
     * @param id the food item ID
     * @return Mono of Food with 200 OK or 404 Not Found
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Food>> getFood(@PathVariable String id) {
        logger.info("REST request to get food item: {}", id);
        return service.findById(id)
                .map(food -> ResponseEntity.ok(food))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Create a new food item
     * POST /api/foods
     * @param food the food item to create
     * @return Mono of created Food with 201 Created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Food> createFood(@Valid @RequestBody Food food) {
        logger.info("REST request to create food item: {}", food.getName());
        food.setId(null); // Ensure new food item
        return service.save(food);
    }

    /**
     * Update an existing food item
     * PUT /api/foods/{id}
     * @param id the food item ID
     * @param food the updated food data
     * @return Mono of updated Food with 200 OK or 404 Not Found
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Food>> updateFood(
            @PathVariable String id,
            @Valid @RequestBody Food food) {
        logger.info("REST request to update food item: {}", id);
        food.setId(id);
        return service.findById(id)
                .flatMap(existing -> service.save(food))
                .map(updated -> ResponseEntity.ok(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete a food item
     * DELETE /api/foods/{id}
     * @param id the food item ID
     * @return Mono with 204 No Content or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteFood(@PathVariable String id) {
        logger.info("REST request to delete food item: {}", id);
        return service.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Get menu for a specific restaurant
     * GET /api/foods/restaurant/{restaurantId}
     * @param restaurantId the restaurant ID
     * @return Flux of food items
     */
    @GetMapping("/restaurant/{restaurantId}")
    public Flux<Food> getRestaurantMenu(@PathVariable String restaurantId) {
        logger.info("REST request to get menu for restaurant: {}", restaurantId);
        return service.findMenuByRestaurantId(restaurantId);
    }

    /**
     * Get available menu for a restaurant
     * GET /api/foods/restaurant/{restaurantId}/available
     * @param restaurantId the restaurant ID
     * @return Flux of available food items
     */
    @GetMapping("/restaurant/{restaurantId}/available")
    public Flux<Food> getAvailableMenu(@PathVariable String restaurantId) {
        logger.info("REST request to get available menu for restaurant: {}", restaurantId);
        return service.findAvailableMenuByRestaurantId(restaurantId);
    }

    /**
     * Get food items by category
     * GET /api/foods/category/{category}
     * @param category the food category
     * @return Flux of food items
     */
    @GetMapping("/category/{category}")
    public Flux<Food> getFoodsByCategory(@PathVariable String category) {
        logger.info("REST request to get food items by category: {}", category);
        return service.findByCategory(category);
    }

    /**
     * Get food items by restaurant and category
     * GET /api/foods/restaurant/{restaurantId}/category/{category}
     * @param restaurantId the restaurant ID
     * @param category the food category
     * @return Flux of food items
     */
    @GetMapping("/restaurant/{restaurantId}/category/{category}")
    public Flux<Food> getFoodsByRestaurantAndCategory(
            @PathVariable String restaurantId,
            @PathVariable String category) {
        logger.info("REST request to get food items for restaurant {} in category {}", restaurantId, category);
        return service.findByRestaurantAndCategory(restaurantId, category);
    }

    /**
     * Get vegetarian food items
     * GET /api/foods/vegetarian
     * @return Flux of vegetarian food items
     */
    @GetMapping("/vegetarian")
    public Flux<Food> getVegetarianFoods() {
        logger.info("REST request to get vegetarian food items");
        return service.findVegetarianFood();
    }

    /**
     * Get vegan food items
     * GET /api/foods/vegan
     * @return Flux of vegan food items
     */
    @GetMapping("/vegan")
    public Flux<Food> getVeganFoods() {
        logger.info("REST request to get vegan food items");
        return service.findVeganFood();
    }

    /**
     * Get available vegetarian items for a restaurant
     * GET /api/foods/restaurant/{restaurantId}/vegetarian
     * @param restaurantId the restaurant ID
     * @return Flux of vegetarian food items
     */
    @GetMapping("/restaurant/{restaurantId}/vegetarian")
    public Flux<Food> getRestaurantVegetarianMenu(@PathVariable String restaurantId) {
        logger.info("REST request to get vegetarian menu for restaurant: {}", restaurantId);
        return service.findAvailableVegetarianByRestaurant(restaurantId);
    }

    /**
     * Get food items within a price range
     * GET /api/foods/price-range?min={min}&max={max}
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return Flux of food items
     */
    @GetMapping("/price-range")
    public Flux<Food> getFoodsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        logger.info("REST request to get food items in price range: {} - {}", minPrice, maxPrice);
        return service.findByPriceRange(minPrice, maxPrice);
    }

    /**
     * Get affordable food items
     * GET /api/foods/affordable?maxPrice={maxPrice}
     * @param maxPrice maximum price
     * @return Flux of affordable food items
     */
    @GetMapping("/affordable")
    public Flux<Food> getAffordableFoods(@RequestParam Double maxPrice) {
        logger.info("REST request to get affordable food items below: {}", maxPrice);
        return service.findAffordableFood(maxPrice);
    }

    /**
     * Search food items by name
     * GET /api/foods/search?name={name}
     * @param name search term
     * @return Flux of food items
     */
    @GetMapping("/search")
    public Flux<Food> searchFoods(@RequestParam String name) {
        logger.info("REST request to search food items by name: {}", name);
        return service.searchByName(name);
    }

    /**
     * Mark food item as available
     * PATCH /api/foods/{id}/available
     * @param id the food item ID
     * @return Mono of updated Food
     */
    @PatchMapping("/{id}/available")
    public Mono<ResponseEntity<Food>> markAsAvailable(@PathVariable String id) {
        logger.info("REST request to mark food item as available: {}", id);
        return service.markAsAvailable(id)
                .map(food -> ResponseEntity.ok(food))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Mark food item as unavailable
     * PATCH /api/foods/{id}/unavailable
     * @param id the food item ID
     * @return Mono of updated Food
     */
    @PatchMapping("/{id}/unavailable")
    public Mono<ResponseEntity<Food>> markAsUnavailable(@PathVariable String id) {
        logger.info("REST request to mark food item as unavailable: {}", id);
        return service.markAsUnavailable(id)
                .map(food -> ResponseEntity.ok(food))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Count food items for a restaurant
     * GET /api/foods/restaurant/{restaurantId}/count
     * @param restaurantId the restaurant ID
     * @return Mono of Long
     */
    @GetMapping("/restaurant/{restaurantId}/count")
    public Mono<Long> countByRestaurant(@PathVariable String restaurantId) {
        logger.info("REST request to count food items for restaurant: {}", restaurantId);
        return service.countByRestaurant(restaurantId);
    }

    /**
     * Delete all food items for a restaurant
     * DELETE /api/foods/restaurant/{restaurantId}
     * @param restaurantId the restaurant ID
     * @return Mono with 204 No Content
     */
    @DeleteMapping("/restaurant/{restaurantId}")
    public Mono<ResponseEntity<Void>> deleteAllByRestaurant(@PathVariable String restaurantId) {
        logger.info("REST request to delete all food items for restaurant: {}", restaurantId);
        return service.deleteAllByRestaurant(restaurantId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}
