/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Service layer for Food entity with business logic and error handling
 */
package com.fooddelivery.service;

import com.fooddelivery.model.Food;
import com.fooddelivery.repository.FoodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for Food entity.
 * Handles business logic for food items and menu management.
 */
@Service
public class FoodService {

    private static final Logger logger = LoggerFactory.getLogger(FoodService.class);

    @Autowired
    private FoodRepository repository;

    /**
     * Find all food items
     * @return Flux of all food items
     */
    public Flux<Food> findAll() {
        logger.info("Fetching all food items");
        return repository.findAll()
                .doOnComplete(() -> logger.info("Successfully fetched all food items"))
                .doOnError(error -> logger.error("Error fetching all food items", error));
    }

    /**
     * Find food item by ID
     * @param id the food item ID
     * @return Mono of Food if found, empty Mono otherwise
     */
    public Mono<Food> findById(String id) {
        logger.info("Fetching food item with ID: {}", id);
        return repository.findById(id)
                .doOnSuccess(food -> {
                    if (food != null) {
                        logger.info("Found food item: {}", food.getName());
                    } else {
                        logger.warn("Food item not found with ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error fetching food item with ID: {}", id, error))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("No food item found with ID: {}", id);
                    return Mono.empty();
                }));
    }

    /**
     * Save (create or update) a food item
     * @param food the food item to save
     * @return Mono of saved Food
     */
    public Mono<Food> save(Food food) {
        if (food.getId() == null) {
            logger.info("Creating new food item: {}", food.getName());
        } else {
            logger.info("Updating food item with ID: {}", food.getId());
        }
        
        return repository.save(food)
                .doOnSuccess(saved -> logger.info("Successfully saved food item: {} with ID: {}", 
                        saved.getName(), saved.getId()))
                .doOnError(error -> logger.error("Error saving food item: {}", food.getName(), error))
                .onErrorResume(error -> {
                    logger.error("Failed to save food item, returning error", error);
                    return Mono.error(new RuntimeException("Failed to save food item: " + error.getMessage()));
                });
    }

    /**
     * Delete food item by ID
     * @param id the food item ID
     * @return Mono of Void when deletion is complete
     */
    public Mono<Void> deleteById(String id) {
        logger.info("Deleting food item with ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("Cannot delete: Food item not found with ID: {}", id);
                    return Mono.error(new RuntimeException("Food item not found with ID: " + id));
                }))
                .flatMap(food -> {
                    logger.info("Deleting food item: {}", food.getName());
                    return repository.deleteById(id);
                })
                .doOnSuccess(unused -> logger.info("Successfully deleted food item with ID: {}", id))
                .doOnError(error -> logger.error("Error deleting food item with ID: {}", id, error));
    }

    /**
     * Find all food items for a specific restaurant (menu)
     * @param restaurantId the restaurant ID
     * @return Flux of food items from the restaurant
     */
    public Flux<Food> findMenuByRestaurantId(String restaurantId) {
        logger.info("Fetching menu for restaurant ID: {}", restaurantId);
        return repository.findByRestaurantId(restaurantId)
                .doOnComplete(() -> logger.info("Successfully fetched menu for restaurant ID: {}", restaurantId))
                .doOnError(error -> logger.error("Error fetching menu for restaurant ID: {}", restaurantId, error));
    }

    /**
     * Find available food items for a restaurant
     * @param restaurantId the restaurant ID
     * @return Flux of available food items
     */
    public Flux<Food> findAvailableMenuByRestaurantId(String restaurantId) {
        logger.info("Fetching available menu for restaurant ID: {}", restaurantId);
        return repository.findByRestaurantIdAndIsAvailable(restaurantId, true)
                .doOnComplete(() -> logger.info("Successfully fetched available menu for restaurant ID: {}", restaurantId))
                .doOnError(error -> logger.error("Error fetching available menu", error));
    }

    /**
     * Find food items by category
     * @param category the food category
     * @return Flux of food items in the category
     */
    public Flux<Food> findByCategory(String category) {
        logger.info("Fetching food items in category: {}", category);
        return repository.findByCategory(category)
                .doOnComplete(() -> logger.info("Successfully fetched food items in category: {}", category))
                .doOnError(error -> logger.error("Error fetching food items by category: {}", category, error));
    }

    /**
     * Find food items by restaurant and category
     * @param restaurantId the restaurant ID
     * @param category the food category
     * @return Flux of food items matching both criteria
     */
    public Flux<Food> findByRestaurantAndCategory(String restaurantId, String category) {
        logger.info("Fetching food items for restaurant: {} in category: {}", restaurantId, category);
        return repository.findByRestaurantIdAndCategory(restaurantId, category)
                .doOnComplete(() -> logger.info("Successfully fetched items for restaurant: {} and category: {}", 
                        restaurantId, category))
                .doOnError(error -> logger.error("Error fetching food items by restaurant and category", error));
    }

    /**
     * Find vegetarian food items
     * @return Flux of vegetarian food items
     */
    public Flux<Food> findVegetarianFood() {
        logger.info("Fetching vegetarian food items");
        return repository.findByIsVegetarian(true)
                .doOnComplete(() -> logger.info("Successfully fetched vegetarian food items"))
                .doOnError(error -> logger.error("Error fetching vegetarian food items", error));
    }

    /**
     * Find vegan food items
     * @return Flux of vegan food items
     */
    public Flux<Food> findVeganFood() {
        logger.info("Fetching vegan food items");
        return repository.findByIsVegan(true)
                .doOnComplete(() -> logger.info("Successfully fetched vegan food items"))
                .doOnError(error -> logger.error("Error fetching vegan food items", error));
    }

    /**
     * Find available vegetarian items for a restaurant
     * @param restaurantId the restaurant ID
     * @return Flux of available vegetarian food items
     */
    public Flux<Food> findAvailableVegetarianByRestaurant(String restaurantId) {
        logger.info("Fetching available vegetarian items for restaurant: {}", restaurantId);
        return repository.findByRestaurantIdAndIsVegetarianAndIsAvailable(restaurantId, true, true)
                .doOnComplete(() -> logger.info("Successfully fetched vegetarian items for restaurant: {}", restaurantId))
                .doOnError(error -> logger.error("Error fetching vegetarian items", error));
    }

    /**
     * Find food items within a price range
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return Flux of food items within the price range
     */
    public Flux<Food> findByPriceRange(Double minPrice, Double maxPrice) {
        logger.info("Fetching food items in price range: {} - {}", minPrice, maxPrice);
        return repository.findByPriceBetween(minPrice, maxPrice)
                .doOnComplete(() -> logger.info("Successfully fetched items in price range"))
                .doOnError(error -> logger.error("Error fetching food items by price range", error));
    }

    /**
     * Find affordable food items (below a certain price)
     * @param maxPrice maximum price
     * @return Flux of affordable food items
     */
    public Flux<Food> findAffordableFood(Double maxPrice) {
        logger.info("Fetching affordable food items below: {}", maxPrice);
        return repository.findByPriceLessThanEqual(maxPrice)
                .doOnComplete(() -> logger.info("Successfully fetched affordable food items"))
                .doOnError(error -> logger.error("Error fetching affordable food items", error));
    }

    /**
     * Search food items by name
     * @param name search term for food name
     * @return Flux of food items with names containing the search term
     */
    public Flux<Food> searchByName(String name) {
        logger.info("Searching food items by name: {}", name);
        return repository.findByNameContainingIgnoreCase(name)
                .doOnComplete(() -> logger.info("Successfully searched food items by name: {}", name))
                .doOnError(error -> logger.error("Error searching food items by name: {}", name, error));
    }

    /**
     * Mark food item as available
     * @param id the food item ID
     * @return Mono of updated Food
     */
    public Mono<Food> markAsAvailable(String id) {
        logger.info("Marking food item as available: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Food item not found with ID: " + id)))
                .flatMap(food -> {
                    food.setIsAvailable(true);
                    return repository.save(food);
                })
                .doOnSuccess(food -> logger.info("Successfully marked food item as available: {}", food.getName()))
                .doOnError(error -> logger.error("Error marking food item as available", error));
    }

    /**
     * Mark food item as unavailable
     * @param id the food item ID
     * @return Mono of updated Food
     */
    public Mono<Food> markAsUnavailable(String id) {
        logger.info("Marking food item as unavailable: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Food item not found with ID: " + id)))
                .flatMap(food -> {
                    food.setIsAvailable(false);
                    return repository.save(food);
                })
                .doOnSuccess(food -> logger.info("Successfully marked food item as unavailable: {}", food.getName()))
                .doOnError(error -> logger.error("Error marking food item as unavailable", error));
    }

    /**
     * Count food items for a restaurant
     * @param restaurantId the restaurant ID
     * @return Mono of Long representing the count
     */
    public Mono<Long> countByRestaurant(String restaurantId) {
        logger.info("Counting food items for restaurant: {}", restaurantId);
        return repository.countByRestaurantId(restaurantId)
                .doOnSuccess(count -> logger.info("Restaurant {} has {} food items", restaurantId, count))
                .doOnError(error -> logger.error("Error counting food items", error));
    }

    /**
     * Delete all food items for a restaurant
     * @param restaurantId the restaurant ID
     * @return Mono of Void when deletion is complete
     */
    public Mono<Void> deleteAllByRestaurant(String restaurantId) {
        logger.info("Deleting all food items for restaurant: {}", restaurantId);
        return repository.deleteByRestaurantId(restaurantId)
                .doOnSuccess(unused -> logger.info("Successfully deleted all food items for restaurant: {}", restaurantId))
                .doOnError(error -> logger.error("Error deleting food items for restaurant: {}", restaurantId, error));
    }
}
