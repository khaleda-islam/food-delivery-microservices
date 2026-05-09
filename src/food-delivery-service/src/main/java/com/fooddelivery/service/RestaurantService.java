/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Service layer for Restaurant entity with business logic and error handling
 */
package com.fooddelivery.service;

import com.fooddelivery.model.Restaurant;
import com.fooddelivery.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for Restaurant entity.
 * Handles business logic and orchestrates repository operations.
 */
@Service
public class RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);

    @Autowired
    private RestaurantRepository repository;

    /**
     * Find all restaurants
     * @return Flux of all restaurants
     */
    public Flux<Restaurant> findAll() {
        logger.info("Fetching all restaurants");
        return repository.findAll()
                .doOnComplete(() -> logger.info("Successfully fetched all restaurants"))
                .doOnError(error -> logger.error("Error fetching all restaurants", error));
    }

    /**
     * Find restaurant by ID
     * @param id the restaurant ID
     * @return Mono of Restaurant if found, empty Mono otherwise
     */
    public Mono<Restaurant> findById(String id) {
        logger.info("Fetching restaurant with ID: {}", id);
        return repository.findById(id)
                .doOnSuccess(restaurant -> {
                    if (restaurant != null) {
                        logger.info("Found restaurant: {}", restaurant.getName());
                    } else {
                        logger.warn("Restaurant not found with ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error fetching restaurant with ID: {}", id, error))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("No restaurant found with ID: {}", id);
                    return Mono.empty();
                }));
    }

    /**
     * Save (create or update) a restaurant
     * @param restaurant the restaurant to save
     * @return Mono of saved Restaurant
     */
    public Mono<Restaurant> save(Restaurant restaurant) {
        if (restaurant.getId() == null) {
            logger.info("Creating new restaurant: {}", restaurant.getName());
        } else {
            logger.info("Updating restaurant with ID: {}", restaurant.getId());
        }
        
        return repository.save(restaurant)
                .doOnSuccess(saved -> logger.info("Successfully saved restaurant: {} with ID: {}", 
                        saved.getName(), saved.getId()))
                .doOnError(error -> logger.error("Error saving restaurant: {}", restaurant.getName(), error))
                .onErrorResume(error -> {
                    logger.error("Failed to save restaurant, returning error", error);
                    return Mono.error(new RuntimeException("Failed to save restaurant: " + error.getMessage()));
                });
    }

    /**
     * Delete restaurant by ID
     * @param id the restaurant ID
     * @return Mono of Void when deletion is complete
     */
    public Mono<Void> deleteById(String id) {
        logger.info("Deleting restaurant with ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("Cannot delete: Restaurant not found with ID: {}", id);
                    return Mono.error(new RuntimeException("Restaurant not found with ID: " + id));
                }))
                .flatMap(restaurant -> {
                    logger.info("Deleting restaurant: {}", restaurant.getName());
                    return repository.deleteById(id);
                })
                .doOnSuccess(unused -> logger.info("Successfully deleted restaurant with ID: {}", id))
                .doOnError(error -> logger.error("Error deleting restaurant with ID: {}", id, error));
    }

    /**
     * Find restaurants by cuisine type
     * @param cuisineType the cuisine type
     * @return Flux of restaurants matching the cuisine type
     */
    public Flux<Restaurant> findByCuisineType(String cuisineType) {
        logger.info("Fetching restaurants with cuisine type: {}", cuisineType);
        return repository.findByCuisineType(cuisineType)
                .doOnComplete(() -> logger.info("Successfully fetched restaurants for cuisine: {}", cuisineType))
                .doOnError(error -> logger.error("Error fetching restaurants by cuisine type: {}", cuisineType, error));
    }

    /**
     * Find restaurants by city
     * @param city the city name
     * @return Flux of restaurants in the city
     */
    public Flux<Restaurant> findByCity(String city) {
        logger.info("Fetching restaurants in city: {}", city);
        return repository.findByCity(city)
                .doOnComplete(() -> logger.info("Successfully fetched restaurants in city: {}", city))
                .doOnError(error -> logger.error("Error fetching restaurants by city: {}", city, error));
    }

    /**
     * Find restaurants with minimum rating
     * @param rating minimum rating
     * @return Flux of restaurants with rating >= specified value
     */
    public Flux<Restaurant> findByMinimumRating(Double rating) {
        logger.info("Fetching restaurants with minimum rating: {}", rating);
        return repository.findByRatingGreaterThanEqual(rating)
                .doOnComplete(() -> logger.info("Successfully fetched restaurants with rating >= {}", rating))
                .doOnError(error -> logger.error("Error fetching restaurants by rating: {}", rating, error));
    }

    /**
     * Find active restaurants
     * @return Flux of active restaurants
     */
    public Flux<Restaurant> findActiveRestaurants() {
        logger.info("Fetching active restaurants");
        return repository.findByIsActive(true)
                .doOnComplete(() -> logger.info("Successfully fetched active restaurants"))
                .doOnError(error -> logger.error("Error fetching active restaurants", error));
    }

    /**
     * Find restaurants by city and cuisine type
     * @param city the city name
     * @param cuisineType the cuisine type
     * @return Flux of restaurants matching both criteria
     */
    public Flux<Restaurant> findByCityAndCuisineType(String city, String cuisineType) {
        logger.info("Fetching restaurants in city: {} with cuisine: {}", city, cuisineType);
        return repository.findByCityAndCuisineType(city, cuisineType)
                .doOnComplete(() -> logger.info("Successfully fetched restaurants for city: {} and cuisine: {}", 
                        city, cuisineType))
                .doOnError(error -> logger.error("Error fetching restaurants by city and cuisine", error));
    }

    /**
     * Search restaurants by name
     * @param name search term for restaurant name
     * @return Flux of restaurants with names containing the search term
     */
    public Flux<Restaurant> searchByName(String name) {
        logger.info("Searching restaurants by name: {}", name);
        return repository.findByNameContainingIgnoreCase(name)
                .doOnComplete(() -> logger.info("Successfully searched restaurants by name: {}", name))
                .doOnError(error -> logger.error("Error searching restaurants by name: {}", name, error));
    }

    /**
     * Find top-rated active restaurants in a city
     * @param city the city name
     * @param minRating minimum rating threshold
     * @return Flux of top-rated active restaurants
     */
    public Flux<Restaurant> findTopRatedRestaurantsInCity(String city, Double minRating) {
        logger.info("Fetching top-rated restaurants in city: {} with minimum rating: {}", city, minRating);
        return repository.findByCityAndRatingGreaterThanEqualAndIsActive(city, minRating, true)
                .doOnComplete(() -> logger.info("Successfully fetched top-rated restaurants in city: {}", city))
                .doOnError(error -> logger.error("Error fetching top-rated restaurants", error));
    }

    /**
     * Validate and activate a restaurant
     * @param id the restaurant ID
     * @return Mono of updated Restaurant
     */
    public Mono<Restaurant> activateRestaurant(String id) {
        logger.info("Activating restaurant with ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Restaurant not found with ID: " + id)))
                .flatMap(restaurant -> {
                    restaurant.setIsActive(true);
                    return repository.save(restaurant);
                })
                .doOnSuccess(restaurant -> logger.info("Successfully activated restaurant: {}", restaurant.getName()))
                .doOnError(error -> logger.error("Error activating restaurant with ID: {}", id, error));
    }

    /**
     * Deactivate a restaurant
     * @param id the restaurant ID
     * @return Mono of updated Restaurant
     */
    public Mono<Restaurant> deactivateRestaurant(String id) {
        logger.info("Deactivating restaurant with ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Restaurant not found with ID: " + id)))
                .flatMap(restaurant -> {
                    restaurant.setIsActive(false);
                    return repository.save(restaurant);
                })
                .doOnSuccess(restaurant -> logger.info("Successfully deactivated restaurant: {}", restaurant.getName()))
                .doOnError(error -> logger.error("Error deactivating restaurant with ID: {}", id, error));
    }
}
