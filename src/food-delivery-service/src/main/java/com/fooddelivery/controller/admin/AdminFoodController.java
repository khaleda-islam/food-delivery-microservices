/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 12, 2026
 * Description: Admin controller for Food management with Thymeleaf views
 */
package com.fooddelivery.controller.admin;

import com.fooddelivery.model.Food;
import com.fooddelivery.service.FoodService;
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
 * Admin controller for managing food items via Thymeleaf interface
 */
@Controller
@RequestMapping("/admin/foods")
public class AdminFoodController {

    private static final Logger logger = LoggerFactory.getLogger(AdminFoodController.class);

    @Autowired
    private FoodService foodService;

    @Autowired
    private RestaurantService restaurantService;

    /**
     * List all food items
     * GET /admin/foods
     */
    @GetMapping
    public String listFoods(Model model, 
                           @RequestParam(required = false) String restaurantId) {
        logger.info("Admin: Listing food items, restaurantId: {}", restaurantId);
        
        if (restaurantId != null && !restaurantId.isEmpty()) {
            model.addAttribute("foods", foodService.findMenuByRestaurantId(restaurantId));
            model.addAttribute("selectedRestaurantId", restaurantId);
        } else {
            model.addAttribute("foods", foodService.findAll());
        }
        
        model.addAttribute("restaurants", restaurantService.findAll());
        return "admin/foods/list";
    }

    /**
     * Show form for creating a new food item
     * GET /admin/foods/new
     */
    @GetMapping("/new")
    public String newFoodForm(Model model, @RequestParam(required = false) String restaurantId) {
        logger.info("Admin: New food form");
        Food food = new Food();
        if (restaurantId != null && !restaurantId.isEmpty()) {
            food.setRestaurantId(restaurantId);
        }
        model.addAttribute("food", food);
        model.addAttribute("restaurants", restaurantService.findAll());
        model.addAttribute("isEdit", false);
        return "admin/foods/form";
    }

    /**
     * Create a new food item
     * POST /admin/foods
     */
    @PostMapping
    public Mono<String> createFood(@Valid @ModelAttribute Food food, 
                                   BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Admin: Validation errors creating food");
            model.addAttribute("restaurants", restaurantService.findAll());
            model.addAttribute("isEdit", false);
            return Mono.just("admin/foods/form");
        }
        
        logger.info("Admin: Creating food: {}", food.getName());
        food.setId(null); // Ensure new food
        return foodService.save(food)
                .doOnSuccess(saved -> logger.info("Admin: Food created with ID: {}", saved.getId()))
                .thenReturn("redirect:/admin/foods");
    }

    /**
     * Show form for editing a food item
     * GET /admin/foods/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public Mono<String> editFoodForm(@PathVariable String id, Model model) {
        logger.info("Admin: Edit food form for ID: {}", id);
        return foodService.findById(id)
                .doOnNext(food -> {
                    model.addAttribute("food", food);
                    model.addAttribute("restaurants", restaurantService.findAll());
                    model.addAttribute("isEdit", true);
                })
                .thenReturn("admin/foods/form")
                .switchIfEmpty(Mono.just("redirect:/admin/foods"));
    }

    /**
     * Update a food item
     * POST /admin/foods/update/{id}
     */
    @PostMapping("/update/{id}")
    public Mono<String> updateFood(@PathVariable String id, 
                                   @Valid @ModelAttribute Food food,
                                   BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Admin: Validation errors updating food");
            model.addAttribute("restaurants", restaurantService.findAll());
            model.addAttribute("isEdit", true);
            return Mono.just("admin/foods/form");
        }
        
        logger.info("Admin: Updating food ID: {}", id);
        food.setId(id);
        return foodService.save(food)
                .doOnSuccess(saved -> logger.info("Admin: Food updated: {}", saved.getId()))
                .thenReturn("redirect:/admin/foods");
    }

    /**
     * Delete a food item
     * GET /admin/foods/delete/{id}
     */
    @GetMapping("/delete/{id}")
    public Mono<String> deleteFood(@PathVariable String id) {
        logger.info("Admin: Deleting food ID: {}", id);
        return foodService.deleteById(id)
                .doOnSuccess(v -> logger.info("Admin: Food deleted: {}", id))
                .thenReturn("redirect:/admin/foods");
    }

    /**
     * View food details
     * GET /admin/foods/{id}
     */
    @GetMapping("/{id}")
    public Mono<String> viewFood(@PathVariable String id, Model model) {
        logger.info("Admin: Viewing food ID: {}", id);
        return foodService.findById(id)
                .flatMap(food -> {
                    model.addAttribute("food", food);
                    // Get restaurant details
                    return restaurantService.findById(food.getRestaurantId())
                            .doOnNext(restaurant -> model.addAttribute("restaurant", restaurant))
                            .thenReturn("admin/foods/view");
                })
                .switchIfEmpty(Mono.just("redirect:/admin/foods"));
    }
}
