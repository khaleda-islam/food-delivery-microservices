/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 12, 2026
 * Description: Admin controller for Order management with Thymeleaf views
 */
package com.fooddelivery.controller.admin;

import com.fooddelivery.model.Order;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.UserService;
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
 * Admin controller for managing orders via Thymeleaf interface
 */
@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    /**
     * List all orders / order history
     * GET /admin/orders
     */
    @GetMapping
    public String listOrders(Model model, 
                            @RequestParam(required = false) String userId,
                            @RequestParam(required = false) String status) {
        logger.info("Admin: Listing orders, userId: {}, status: {}", userId, status);
        
        if (userId != null && !userId.isEmpty()) {
            model.addAttribute("orders", orderService.findOrdersByUser(userId));
            model.addAttribute("selectedUserId", userId);
        } else if (status != null && !status.isEmpty()) {
            model.addAttribute("orders", orderService.findOrdersByStatus(status));
            model.addAttribute("selectedStatus", status);
        } else {
            model.addAttribute("orders", orderService.findAll());
        }
        
        model.addAttribute("users", userService.findAll());
        return "admin/orders/list";
    }

    /**
     * Show form for creating a new order
     * GET /admin/orders/new
     */
    @GetMapping("/new")
    public String newOrderForm(Model model) {
        logger.info("Admin: New order form");
        model.addAttribute("order", new Order());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("restaurants", restaurantService.findAll());
        model.addAttribute("isEdit", false);
        return "admin/orders/form";
    }

    /**
     * Create a new order
     * POST /admin/orders
     */
    @PostMapping
    public Mono<String> createOrder(@Valid @ModelAttribute Order order, 
                                    BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Admin: Validation errors creating order");
            model.addAttribute("users", userService.findAll());
            model.addAttribute("restaurants", restaurantService.findAll());
            model.addAttribute("isEdit", false);
            return Mono.just("admin/orders/form");
        }
        
        logger.info("Admin: Creating order for user: {}", order.getUserId());
        order.setId(null); // Ensure new order
        return orderService.placeOrder(order)
                .doOnSuccess(saved -> logger.info("Admin: Order created with ID: {}", saved.getId()))
                .thenReturn("redirect:/admin/orders");
    }

    /**
     * Show form for editing an order
     * GET /admin/orders/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public Mono<String> editOrderForm(@PathVariable String id, Model model) {
        logger.info("Admin: Edit order form for ID: {}", id);
        return orderService.findById(id)
                .doOnNext(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("users", userService.findAll());
                    model.addAttribute("restaurants", restaurantService.findAll());
                    model.addAttribute("isEdit", true);
                })
                .thenReturn("admin/orders/form")
                .switchIfEmpty(Mono.just("redirect:/admin/orders"));
    }

    /**
     * Update an order
     * POST /admin/orders/update/{id}
     */
    @PostMapping("/update/{id}")
    public Mono<String> updateOrder(@PathVariable String id, 
                                    @Valid @ModelAttribute Order order,
                                    BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Admin: Validation errors updating order");
            model.addAttribute("users", userService.findAll());
            model.addAttribute("restaurants", restaurantService.findAll());
            model.addAttribute("isEdit", true);
            return Mono.just("admin/orders/form");
        }
        
        logger.info("Admin: Updating order ID: {}", id);
        order.setId(id);
        // For order updates, we use placeOrder which handles the order logic
        return orderService.placeOrder(order)
                .doOnSuccess(saved -> logger.info("Admin: Order updated: {}", saved.getId()))
                .thenReturn("redirect:/admin/orders");
    }

    /**
     * Update order status
     * POST /admin/orders/{id}/status
     */
    @PostMapping("/{id}/status")
    public Mono<String> updateOrderStatus(@PathVariable String id, 
                                          @RequestParam String status) {
        logger.info("Admin: Updating order {} status to: {}", id, status);
        return orderService.updateOrderStatus(id, status)
                .doOnSuccess(order -> logger.info("Admin: Order status updated: {}", id))
                .thenReturn("redirect:/admin/orders/" + id)
                .onErrorResume(e -> {
                    logger.error("Admin: Error updating order status", e);
                    return Mono.just("redirect:/admin/orders");
                });
    }

    /**
     * Delete an order
     * GET /admin/orders/delete/{id}
     */
    @GetMapping("/delete/{id}")
    public Mono<String> deleteOrder(@PathVariable String id) {
        logger.info("Admin: Deleting order ID: {}", id);
        return orderService.deleteById(id)
                .doOnSuccess(v -> logger.info("Admin: Order deleted: {}", id))
                .thenReturn("redirect:/admin/orders");
    }

    /**
     * View order details
     * GET /admin/orders/{id}
     */
    @GetMapping("/{id}")
    public Mono<String> viewOrder(@PathVariable String id, Model model) {
        logger.info("Admin: Viewing order ID: {}", id);
        return orderService.findById(id)
                .flatMap(order -> {
                    model.addAttribute("order", order);
                    // Get user details
                    return userService.findById(order.getUserId())
                            .doOnNext(user -> model.addAttribute("customer", user))
                            .then(restaurantService.findById(order.getRestaurantId()))
                            .doOnNext(restaurant -> model.addAttribute("restaurant", restaurant))
                            .thenReturn("admin/orders/view");
                })
                .switchIfEmpty(Mono.just("redirect:/admin/orders"));
    }
}
