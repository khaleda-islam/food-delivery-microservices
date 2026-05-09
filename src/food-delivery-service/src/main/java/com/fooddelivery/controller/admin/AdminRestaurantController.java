/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 12, 2026
 * Description: Admin controller for Restaurant management with Thymeleaf views
 */
package com.fooddelivery.controller.admin;

import com.fooddelivery.model.Restaurant;
import com.fooddelivery.service.RestaurantService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Admin controller for managing restaurants via Thymeleaf interface
 */
@Controller
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(AdminRestaurantController.class);

    @Autowired
    private RestaurantService service;

    /**
     * List all restaurants
     * GET /admin/restaurants
     */
    @GetMapping
    public String listRestaurants(Model model) {
        logger.info("Admin: Listing all restaurants");
        model.addAttribute("restaurants", service.findAll());
        return "admin/restaurants/list";
    }

    /**
     * Show form for creating a new restaurant
     * GET /admin/restaurants/new
     */
    @GetMapping("/new")
    public String newRestaurantForm(Model model) {
        logger.info("Admin: New restaurant form");
        model.addAttribute("restaurant", new Restaurant());
        model.addAttribute("isEdit", false);
        return "admin/restaurants/form";
    }

    /**
     * Create a new restaurant
     * POST /admin/restaurants
     */
    @PostMapping
    public Mono<String> createRestaurant(@Valid @ModelAttribute Restaurant restaurant, 
                                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Admin: Validation errors creating restaurant");
            model.addAttribute("isEdit", false);
            return Mono.just("admin/restaurants/form");
        }
        
        logger.info("Admin: Creating restaurant: {}", restaurant.getName());
        restaurant.setId(null); // Ensure new restaurant
        return service.save(restaurant)
                .doOnSuccess(saved -> logger.info("Admin: Restaurant created with ID: {}", saved.getId()))
                .thenReturn("redirect:/admin/restaurants");
    }

    /**
     * Show form for editing a restaurant
     * GET /admin/restaurants/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public Mono<String> editRestaurantForm(@PathVariable String id, Model model) {
        logger.info("Admin: Edit restaurant form for ID: {}", id);
        return service.findById(id)
                .doOnNext(restaurant -> {
                    model.addAttribute("restaurant", restaurant);
                    model.addAttribute("isEdit", true);
                })
                .thenReturn("admin/restaurants/form")
                .switchIfEmpty(Mono.just("redirect:/admin/restaurants"));
    }

    /**
     * Update a restaurant
     * POST /admin/restaurants/update/{id}
     */
    @PostMapping("/update/{id}")
    public Mono<String> updateRestaurant(@PathVariable String id, 
                                         @Valid @ModelAttribute Restaurant restaurant,
                                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Admin: Validation errors updating restaurant");
            model.addAttribute("isEdit", true);
            return Mono.just("admin/restaurants/form");
        }
        
        logger.info("Admin: Updating restaurant ID: {}", id);
        restaurant.setId(id);
        return service.save(restaurant)
                .doOnSuccess(saved -> logger.info("Admin: Restaurant updated: {}", saved.getId()))
                .thenReturn("redirect:/admin/restaurants");
    }

    /**
     * Delete a restaurant
     * GET /admin/restaurants/delete/{id}
     */
    @GetMapping("/delete/{id}")
    public Mono<String> deleteRestaurant(@PathVariable String id) {
        logger.info("Admin: Deleting restaurant ID: {}", id);
        return service.deleteById(id)
                .doOnSuccess(v -> logger.info("Admin: Restaurant deleted: {}", id))
                .thenReturn("redirect:/admin/restaurants");
    }

    /**
     * View restaurant details
     * GET /admin/restaurants/{id}
     */
    @GetMapping("/{id}")
    public Mono<String> viewRestaurant(@PathVariable String id, Model model) {
        logger.info("Admin: Viewing restaurant ID: {}", id);
        return service.findById(id)
                .doOnNext(restaurant -> model.addAttribute("restaurant", restaurant))
                .thenReturn("admin/restaurants/view")
                .switchIfEmpty(Mono.just("redirect:/admin/restaurants"));
    }
}
