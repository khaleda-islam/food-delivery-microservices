/*
 * API Gateway Service - Order Controller
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * 
 * Purpose: Public REST API controller for order operations.
 * Exposes endpoints to React frontend, proxies requests to Food Delivery Service.
 */

package com.fooddelivery.controller;

import com.fooddelivery.dto.GuestCheckoutRequest;
import com.fooddelivery.model.Order;
import com.fooddelivery.service.OrderClientService;
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
 * Public-facing REST controller for Order operations.
 * 
 * This controller provides the API Gateway's public interface for order management.
 * All requests are proxied to the Food Delivery Service through the client service.
 * 
 * CORS Configuration:
 * - Allows requests from React dev server (http://localhost:3000)
 * 
 * Base URL: /api/orders
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderClientService orderClientService;

    /**
     * Get all orders
     * 
     * Endpoint: GET /api/orders
     * React Usage: Admin feature to view all orders
     * 
     * @return Flux of all orders
     */
    @GetMapping
    public Flux<Order> getAllOrders() {
        logger.info("API Gateway: GET /api/orders");
        return orderClientService.getAllOrders();
    }

    /**
     * Get order by ID
     * 
     * Endpoint: GET /api/orders/{id}
     * React Usage: Display order details and tracking
     * 
     * @param id the order ID
     * @return Mono of Order with 200 OK or 404 Not Found
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Order>> getOrder(@PathVariable String id) {
        logger.info("API Gateway: GET /api/orders/{}", id);
        return orderClientService.getOrderById(id)
                .map(order -> ResponseEntity.ok(order))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Create a new order (place order)
     * 
     * Endpoint: POST /api/orders
     * React Usage: Checkout - place new order
     * 
     * @param order the order to create
     * @return Mono of created Order with 201 Created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> createOrder(@Valid @RequestBody Order order) {
        logger.info("API Gateway: POST /api/orders - User: {}, Total: ${}", 
                    order.getUserId(), order.getTotalPrice());
        return orderClientService.createOrder(order);
    }

    /**
     * Guest checkout - create user and order in one request
     * 
     * Endpoint: POST /api/orders/guest-checkout
     * React Usage: Guest checkout without login
     * 
     * @param request DTO containing customer info and order data
     * @return Mono of created Order with 201 Created
     */
    @PostMapping("/guest-checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> guestCheckout(@Valid @RequestBody GuestCheckoutRequest request) {
        logger.info("API Gateway: POST /api/orders/guest-checkout - Email: {}",
                    request.getCustomerEmail());
        return orderClientService.guestCheckout(request);
    }

    /**
     * Update an existing order
     * 
     * Endpoint: PUT /api/orders/{id}
     * React Usage: Modify order before confirmation
     * 
     * @param id the order ID
     * @param order the updated order data
     * @return Mono of updated Order with 200 OK or 404 Not Found
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Order>> updateOrder(
            @PathVariable String id,
            @Valid @RequestBody Order order) {
        logger.info("API Gateway: PUT /api/orders/{}", id);
        return orderClientService.updateOrder(id, order)
                .map(updated -> ResponseEntity.ok(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Delete an order (cancel)
     * 
     * Endpoint: DELETE /api/orders/{id}
     * React Usage: Cancel order feature
     * 
     * @param id the order ID
     * @return Mono with 204 No Content or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOrder(@PathVariable String id) {
        logger.info("API Gateway: DELETE /api/orders/{}", id);
        return orderClientService.deleteOrder(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Get orders by user ID
     * 
     * Endpoint: GET /api/orders/user/{userId}
     * React Usage: Display user's order history
     * 
     * @param userId the user ID
     * @return Flux of orders for that user
     */
    @GetMapping("/user/{userId}")
    public Flux<Order> getOrdersByUserId(@PathVariable String userId) {
        logger.info("API Gateway: GET /api/orders/user/{}", userId);
        return orderClientService.getOrdersByUserId(userId);
    }

    /**
     * Get orders by restaurant ID
     * 
     * Endpoint: GET /api/orders/restaurant/{restaurantId}
     * React Usage: Restaurant dashboard - view incoming orders
     * 
     * @param restaurantId the restaurant ID
     * @return Flux of orders for that restaurant
     */
    @GetMapping("/restaurant/{restaurantId}")
    public Flux<Order> getOrdersByRestaurantId(@PathVariable String restaurantId) {
        logger.info("API Gateway: GET /api/orders/restaurant/{}", restaurantId);
        return orderClientService.getOrdersByRestaurantId(restaurantId);
    }

    /**
     * Get orders by status
     * 
     * Endpoint: GET /api/orders/status/{status}
     * React Usage: Filter orders by status (PENDING, CONFIRMED, etc.)
     * 
     * @param status the order status
     * @return Flux of orders with that status
     */
    @GetMapping("/status/{status}")
    public Flux<Order> getOrdersByStatus(@PathVariable String status) {
        logger.info("API Gateway: GET /api/orders/status/{}", status);
        return orderClientService.getOrdersByStatus(status);
    }

    /**
     * Update order status
     * 
     * Endpoint: PUT /api/orders/{id}/status/{status}
     * React Usage: Restaurant dashboard - update order progress
     * 
     * @param id the order ID
     * @param status the new status
     * @return Mono of updated Order with 200 OK or 404 Not Found
     */
    @PutMapping("/{id}/status/{status}")
    public Mono<ResponseEntity<Order>> updateOrderStatus(
            @PathVariable String id,
            @PathVariable String status) {
        logger.info("API Gateway: PUT /api/orders/{}/status/{}", id, status);
        return orderClientService.updateOrderStatus(id, status)
                .map(updated -> ResponseEntity.ok(updated))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Get pending orders
     * 
     * Endpoint: GET /api/orders/pending
     * React Usage: Show orders awaiting confirmation
     * 
     * @return Flux of pending orders
     */
    @GetMapping("/pending")
    public Flux<Order> getPendingOrders() {
        logger.info("API Gateway: GET /api/orders/pending");
        return orderClientService.getPendingOrders();
    }

    /**
     * Get active orders
     * 
     * Endpoint: GET /api/orders/active
     * React Usage: Show orders in progress (pending or confirmed)
     * 
     * @return Flux of active orders
     */
    @GetMapping("/active")
    public Flux<Order> getActiveOrders() {
        logger.info("API Gateway: GET /api/orders/active");
        return orderClientService.getActiveOrders();
    }
}
