/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: REST controller for Order entity with reactive endpoints
 */
package com.fooddelivery.controller;

import com.fooddelivery.dto.GuestCheckoutRequest;
import com.fooddelivery.model.Order;
import com.fooddelivery.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * REST controller for managing orders.
 * Provides reactive API endpoints for order operations including placement and tracking.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService service;

    /**
     * Get all orders
     * GET /api/orders
     * @return Flux of all orders
     */
            @GetMapping
    public Flux<Order> getAllOrders() {
        logger.info("REST request to get all orders");
        return service.findAll();
    }

    /**
     * Get order by ID
     * GET /api/orders/{id}
     * @param id the order ID
     * @return Mono of Order with 200 OK or 404 Not Found
     */
            @GetMapping("/{id}")
    public Mono<ResponseEntity<Order>> getOrder(
            @PathVariable String id) {
        logger.info("REST request to get order: {}", id);
        return service.findById(id)
                .map(order -> ResponseEntity.ok(order))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Place a new order
     * POST /api/orders
     * @param order the order to place
     * @return Mono of created Order with 201 Created
     */
            @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> placeOrder(
            @Valid @RequestBody Order order) {
        logger.info("REST request to place order for user: {}", order.getUserId());
        return service.placeOrder(order);
    }

    /**
     * Place order for guest user (auto-create user if doesn't exist)
     * POST /api/orders/guest-checkout
     * @param request DTO containing combined user and order data
     * @return Mono of created Order with 201 Created
     */
            @PostMapping("/guest-checkout")
    public Mono<ResponseEntity<Order>> guestCheckout(
            @Valid @RequestBody GuestCheckoutRequest request) {
        logger.info("REST request for guest checkout - Email: {}", request.getCustomerEmail());
        return service.processGuestCheckout(request)
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(order))
                .onErrorResume(error -> {
                    logger.error("Error in guest checkout: {}", error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    /**
     * Update order status
     * PATCH /api/orders/{id}/status
     * @param id the order ID
     * @param statusData map containing the new status
     * @return Mono of updated Order
     */
            @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<Order>> updateOrderStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> statusData) {
        String newStatus = statusData.get("status");
        logger.info("REST request to update order {} status to: {}", id, newStatus);
        return service.updateOrderStatus(id, newStatus)
                .map(order -> ResponseEntity.ok(order))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Update payment status
     * PATCH /api/orders/{id}/payment-status
     * @param id the order ID
     * @param paymentData map containing the payment status
     * @return Mono of updated Order
     */
    @PatchMapping("/{id}/payment-status")
    public Mono<ResponseEntity<Order>> updatePaymentStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> paymentData) {
        String paymentStatus = paymentData.get("paymentStatus");
        logger.info("REST request to update order {} payment status to: {}", id, paymentStatus);
        return service.updatePaymentStatus(id, paymentStatus)
                .map(order -> ResponseEntity.ok(order))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Cancel an order
     * PATCH /api/orders/{id}/cancel
     * @param id the order ID
     * @param cancelData map containing cancellation reason
     * @return Mono of cancelled Order
     */
    @PatchMapping("/{id}/cancel")
    public Mono<ResponseEntity<Order>> cancelOrder(
            @PathVariable String id,
            @RequestBody Map<String, String> cancelData) {
        String reason = cancelData.getOrDefault("reason", "Customer requested cancellation");
        logger.info("REST request to cancel order: {}", id);
        return service.cancelOrder(id, reason)
                .map(order -> ResponseEntity.ok(order))
                .onErrorResume(e -> {
                    logger.error("Error cancelling order: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Assign delivery partner to order
     * PATCH /api/orders/{id}/assign-delivery
     * @param id the order ID
     * @param assignmentData map containing delivery partner ID
     * @return Mono of updated Order
     */
    @PatchMapping("/{id}/assign-delivery")
    public Mono<ResponseEntity<Order>> assignDeliveryPartner(
            @PathVariable String id,
            @RequestBody Map<String, String> assignmentData) {
        String deliveryPartnerId = assignmentData.get("deliveryPartnerId");
        logger.info("REST request to assign delivery partner {} to order {}", deliveryPartnerId, id);
        return service.assignDeliveryPartner(id, deliveryPartnerId)
                .map(order -> ResponseEntity.ok(order))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Get orders for a specific user
     * GET /api/orders/user/{userId}
     * @param userId the user's ID
     * @return Flux of orders
     */
    @GetMapping("/user/{userId}")
    public Flux<Order> getUserOrders(@PathVariable String userId) {
        logger.info("REST request to get orders for user: {}", userId);
        return service.findOrdersByUser(userId);
    }

    /**
     * Get recent orders for a user
     * GET /api/orders/user/{userId}/recent
     * @param userId the user's ID
     * @return Flux of recent orders (sorted by date desc)
     */
    @GetMapping("/user/{userId}/recent")
    public Flux<Order> getRecentUserOrders(@PathVariable String userId) {
        logger.info("REST request to get recent orders for user: {}", userId);
        return service.findRecentOrdersByUser(userId);
    }

    /**
     * Get orders for a specific restaurant
     * GET /api/orders/restaurant/{restaurantId}
     * @param restaurantId the restaurant's ID
     * @return Flux of orders
     */
    @GetMapping("/restaurant/{restaurantId}")
    public Flux<Order> getRestaurantOrders(@PathVariable String restaurantId) {
        logger.info("REST request to get orders for restaurant: {}", restaurantId);
        return service.findOrdersByRestaurant(restaurantId);
    }

    /**
     * Get recent orders for a restaurant
     * GET /api/orders/restaurant/{restaurantId}/recent
     * @param restaurantId the restaurant's ID
     * @return Flux of recent orders
     */
    @GetMapping("/restaurant/{restaurantId}/recent")
    public Flux<Order> getRecentRestaurantOrders(@PathVariable String restaurantId) {
        logger.info("REST request to get recent orders for restaurant: {}", restaurantId);
        return service.findRecentOrdersByRestaurant(restaurantId);
    }

    /**
     * Get orders by status
     * GET /api/orders/status/{status}
     * @param status the order status
     * @return Flux of orders
     */
    @GetMapping("/status/{status}")
    public Flux<Order> getOrdersByStatus(@PathVariable String status) {
        logger.info("REST request to get orders by status: {}", status);
        return service.findOrdersByStatus(status);
    }

    /**
     * Get orders for a delivery partner
     * GET /api/orders/delivery-partner/{deliveryPartnerId}
     * @param deliveryPartnerId the delivery partner's ID
     * @return Flux of orders
     */
    @GetMapping("/delivery-partner/{deliveryPartnerId}")
    public Flux<Order> getDeliveryPartnerOrders(@PathVariable String deliveryPartnerId) {
        logger.info("REST request to get orders for delivery partner: {}", deliveryPartnerId);
        return service.findOrdersByDeliveryPartner(deliveryPartnerId);
    }

    /**
     * Get active orders for a delivery partner
     * GET /api/orders/delivery-partner/{deliveryPartnerId}/active
     * @param deliveryPartnerId the delivery partner's ID
     * @return Flux of active orders
     */
    @GetMapping("/delivery-partner/{deliveryPartnerId}/active")
    public Flux<Order> getActiveDeliveryOrders(@PathVariable String deliveryPartnerId) {
        logger.info("REST request to get active orders for delivery partner: {}", deliveryPartnerId);
        return service.findActiveOrdersForDeliveryPartner(deliveryPartnerId);
    }

    /**
     * Get orders within a date range
     * GET /api/orders/date-range?start={start}&end={end}
     * @param startDate start date/time
     * @param endDate end date/time
     * @return Flux of orders
     */
    @GetMapping("/date-range")
    public Flux<Order> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        logger.info("REST request to get orders between {} and {}", startDate, endDate);
        return service.findOrdersByDateRange(startDate, endDate);
    }

    /**
     * Get user orders within a date range
     * GET /api/orders/user/{userId}/date-range?start={start}&end={end}
     * @param userId the user's ID
     * @param startDate start date/time
     * @param endDate end date/time
     * @return Flux of orders
     */
    @GetMapping("/user/{userId}/date-range")
    public Flux<Order> getUserOrdersByDateRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        logger.info("REST request to get orders for user {} between {} and {}", userId, startDate, endDate);
        return service.findUserOrdersByDateRange(userId, startDate, endDate);
    }

    /**
     * Count orders for a user
     * GET /api/orders/user/{userId}/count
     * @param userId the user's ID
     * @return Mono of Long
     */
    @GetMapping("/user/{userId}/count")
    public Mono<Long> countUserOrders(@PathVariable String userId) {
        logger.info("REST request to count orders for user: {}", userId);
        return service.countOrdersByUser(userId);
    }

    /**
     * Count orders for a restaurant
     * GET /api/orders/restaurant/{restaurantId}/count
     * @param restaurantId the restaurant's ID
     * @return Mono of Long
     */
    @GetMapping("/restaurant/{restaurantId}/count")
    public Mono<Long> countRestaurantOrders(@PathVariable String restaurantId) {
        logger.info("REST request to count orders for restaurant: {}", restaurantId);
        return service.countOrdersByRestaurant(restaurantId);
    }

    /**
     * Count orders by status
     * GET /api/orders/status/{status}/count
     * @param status the order status
     * @return Mono of Long
     */
    @GetMapping("/status/{status}/count")
    public Mono<Long> countOrdersByStatus(@PathVariable String status) {
        logger.info("REST request to count orders by status: {}", status);
        return service.countOrdersByStatus(status);
    }

    /**
     * Get restaurant order statistics
     * GET /api/orders/restaurant/{restaurantId}/statistics?start={start}&end={end}
     * @param restaurantId the restaurant's ID
     * @param startDate start date/time
     * @param endDate end date/time
     * @return Mono of statistics summary
     */
    @GetMapping("/restaurant/{restaurantId}/statistics")
    public Mono<Map<String, String>> getRestaurantStatistics(
            @PathVariable String restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        logger.info("REST request to get statistics for restaurant: {}", restaurantId);
        return service.getRestaurantOrderStatistics(restaurantId, startDate, endDate)
                .map(stats -> Map.of("statistics", stats));
    }

    /**
     * Delete an order
     * DELETE /api/orders/{id}
     * @param id the order ID
     * @return Mono with 204 No Content or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOrder(@PathVariable String id) {
        logger.info("REST request to delete order: {}", id);
        return service.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
