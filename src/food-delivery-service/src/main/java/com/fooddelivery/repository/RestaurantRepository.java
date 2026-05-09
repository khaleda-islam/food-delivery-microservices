/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Reactive repository for Restaurant entity with custom query methods
 */
package com.fooddelivery.repository;

import com.fooddelivery.model.Restaurant;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive repository for Restaurant entity.
 * Provides CRUD operations and custom query methods using reactive types.
 */
@Repository
public interface RestaurantRepository extends ReactiveMongoRepository<Restaurant, String> {

    /**
     * Find all restaurants by cuisine type
     * @param cuisineType the type of cuisine (e.g., "Italian", "Chinese", "Indian")
     * @return Flux of restaurants matching the cuisine type
     */
    Flux<Restaurant> findByCuisineType(String cuisineType);

    /**
     * Find all restaurants in a specific city
     * @param city the city name
     * @return Flux of restaurants in the specified city
     */
    Flux<Restaurant> findByCity(String city);

    /**
     * Find all restaurants with rating greater than or equal to specified value
     * @param rating minimum rating (0.0 to 5.0)
     * @return Flux of restaurants with rating >= specified value
     */
    Flux<Restaurant> findByRatingGreaterThanEqual(Double rating);

    /**
     * Find all active restaurants
     * @param isActive true to find active restaurants, false for inactive
     * @return Flux of restaurants matching the active status
     */
    Flux<Restaurant> findByIsActive(Boolean isActive);

    /**
     * Find restaurants by city and cuisine type
     * @param city the city name
     * @param cuisineType the type of cuisine
     * @return Flux of restaurants matching both criteria
     */
    Flux<Restaurant> findByCityAndCuisineType(String city, String cuisineType);

    /**
     * Find restaurants by name containing a search term (case-insensitive)
     * @param name search term for restaurant name
     * @return Flux of restaurants with names containing the search term
     */
    Flux<Restaurant> findByNameContainingIgnoreCase(String name);

    /**
     * Find active restaurants in a city with minimum rating
     * @param city the city name
     * @param rating minimum rating
     * @param isActive active status
     * @return Flux of restaurants matching all criteria
     */
    Flux<Restaurant> findByCityAndRatingGreaterThanEqualAndIsActive(String city, Double rating, Boolean isActive);

    /**
     * Find a restaurant by name (exact match, case-insensitive)
     * @param name the restaurant name
     * @return Mono of Restaurant if found
     */
    Mono<Restaurant> findByNameIgnoreCase(String name);
}
