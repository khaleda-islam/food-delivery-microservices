/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Reactive repository for Food entity with custom query methods
 */
package com.fooddelivery.repository;

import com.fooddelivery.model.Food;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive repository for Food entity.
 * Provides CRUD operations and custom query methods using reactive types.
 */
@Repository
public interface FoodRepository extends ReactiveMongoRepository<Food, String> {

    /**
     * Find all food items for a specific restaurant
     * @param restaurantId the restaurant's ID
     * @return Flux of food items from the specified restaurant
     */
    Flux<Food> findByRestaurantId(String restaurantId);

    /**
     * Find all food items by category
     * @param category the food category (e.g., "Appetizer", "Main Course", "Dessert")
     * @return Flux of food items in the specified category
     */
    Flux<Food> findByCategory(String category);

    /**
     * Find all available food items
     * @param isAvailable true to find available items, false for unavailable
     * @return Flux of food items matching the availability status
     */
    Flux<Food> findByIsAvailable(Boolean isAvailable);

    /**
     * Find all vegetarian food items
     * @param isVegetarian true to find vegetarian items
     * @return Flux of vegetarian food items
     */
    Flux<Food> findByIsVegetarian(Boolean isVegetarian);

    /**
     * Find all vegan food items
     * @param isVegan true to find vegan items
     * @return Flux of vegan food items
     */
    Flux<Food> findByIsVegan(Boolean isVegan);

    /**
     * Find food items within a price range
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return Flux of food items within the specified price range
     */
    Flux<Food> findByPriceBetween(Double minPrice, Double maxPrice);

    /**
     * Find food items by name containing a search term (case-insensitive)
     * @param name search term for food name
     * @return Flux of food items with names containing the search term
     */
    Flux<Food> findByNameContainingIgnoreCase(String name);

    /**
     * Find available food items for a specific restaurant
     * @param restaurantId the restaurant's ID
     * @param isAvailable availability status
     * @return Flux of food items matching both criteria
     */
    Flux<Food> findByRestaurantIdAndIsAvailable(String restaurantId, Boolean isAvailable);

    /**
     * Find food items by restaurant and category
     * @param restaurantId the restaurant's ID
     * @param category the food category
     * @return Flux of food items matching both criteria
     */
    Flux<Food> findByRestaurantIdAndCategory(String restaurantId, String category);

    /**
     * Find food items below a specific price
     * @param price maximum price
     * @return Flux of food items with price less than or equal to specified value
     */
    Flux<Food> findByPriceLessThanEqual(Double price);

    /**
     * Find available vegetarian food items for a restaurant
     * @param restaurantId the restaurant's ID
     * @param isVegetarian vegetarian status
     * @param isAvailable availability status
     * @return Flux of food items matching all criteria
     */
    Flux<Food> findByRestaurantIdAndIsVegetarianAndIsAvailable(String restaurantId, Boolean isVegetarian, Boolean isAvailable);

    /**
     * Count food items by restaurant ID
     * @param restaurantId the restaurant's ID
     * @return Mono of Long representing the count
     */
    Mono<Long> countByRestaurantId(String restaurantId);

    /**
     * Delete all food items for a specific restaurant
     * @param restaurantId the restaurant's ID
     * @return Mono of Void when deletion is complete
     */
    Mono<Void> deleteByRestaurantId(String restaurantId);
}
