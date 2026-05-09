/*
 * API Gateway Service - Order Client Service
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * 
 * Purpose: Client service to call Food Delivery Service order endpoints.
 * Uses WebClient with Eureka service discovery to communicate with the backend.
 */

package com.fooddelivery.service;

import com.fooddelivery.dto.GuestCheckoutRequest;
import com.fooddelivery.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client service for Order operations.
 * 
 * This service acts as a proxy to the Food Delivery Service order endpoints.
 * Handles order placement, tracking, and management.
 * 
 * Service URL Pattern: http://food-delivery-service/api/orders
 */
@Service
public class OrderClientService {

    private static final Logger logger = LoggerFactory.getLogger(OrderClientService.class);
    private static final String SERVICE_URL = "http://food-delivery-service/api/orders";

    @Autowired
    private WebClient webClient;

    /**
     * Get all orders
     * 
     * @return Flux of all orders
     */
    public Flux<Order> getAllOrders() {
        logger.info("Calling Food Delivery Service: GET {}", SERVICE_URL);
        return webClient.get()
                .uri(SERVICE_URL)
                .retrieve()
                .bodyToFlux(Order.class)
                .doOnError(error -> logger.error("Error fetching all orders: {}", error.getMessage()));
    }

    /**
     * Get a specific order by ID
     * 
     * @param id the order ID
     * @return Mono of Order
     */
    public Mono<Order> getOrderById(String id) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Order.class)
                .doOnError(error -> logger.error("Error fetching order {}: {}", id, error.getMessage()));
    }

    /**
     * Create a new order (place order)
     * 
     * @param order the order to create
     * @return Mono of created Order
     */
    public Mono<Order> createOrder(Order order) {
        logger.info("Calling Food Delivery Service: POST {} - User: {}", SERVICE_URL, order.getUserId());
        return webClient.post()
                .uri(SERVICE_URL)
                .bodyValue(order)
                .retrieve()
                .bodyToMono(Order.class)
                .doOnError(error -> logger.error("Error creating order: {}", error.getMessage()));
    }

    /**
     * Update an existing order
     * 
     * @param id the order ID
     * @param order the updated order data
     * @return Mono of updated Order
     */
    public Mono<Order> updateOrder(String id, Order order) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: PUT {}", url);
        return webClient.put()
                .uri(url)
                .bodyValue(order)
                .retrieve()
                .bodyToMono(Order.class)
                .doOnError(error -> logger.error("Error updating order {}: {}", id, error.getMessage()));
    }

    /**
     * Delete an order (cancel)
     * 
     * @param id the order ID
     * @return Mono of Void
     */
    public Mono<Void> deleteOrder(String id) {
        String url = SERVICE_URL + "/" + id;
        logger.info("Calling Food Delivery Service: DELETE {}", url);
        return webClient.delete()
                .uri(url)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> logger.error("Error deleting order {}: {}", id, error.getMessage()));
    }

    /**
     * Get orders by user ID
     * 
     * @param userId the user ID
     * @return Flux of orders for that user
     */
    public Flux<Order> getOrdersByUserId(String userId) {
        String url = SERVICE_URL + "/user/" + userId;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Order.class)
                .doOnError(error -> logger.error("Error fetching orders for user {}: {}", userId, error.getMessage()));
    }

    /**
     * Get orders by restaurant ID
     * 
     * @param restaurantId the restaurant ID
     * @return Flux of orders for that restaurant
     */
    public Flux<Order> getOrdersByRestaurantId(String restaurantId) {
        String url = SERVICE_URL + "/restaurant/" + restaurantId;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Order.class)
                .doOnError(error -> logger.error("Error fetching orders for restaurant {}: {}", restaurantId, error.getMessage()));
    }

    /**
     * Get orders by status
     * 
     * @param status the order status
     * @return Flux of orders with that status
     */
    public Flux<Order> getOrdersByStatus(String status) {
        String url = SERVICE_URL + "/status/" + status;
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Order.class)
                .doOnError(error -> logger.error("Error fetching orders by status {}: {}", status, error.getMessage()));
    }

    /**
     * Update order status
     * 
     * @param id the order ID
     * @param status the new status
     * @return Mono of updated Order
     */
    public Mono<Order> updateOrderStatus(String id, String status) {
        String url = SERVICE_URL + "/" + id + "/status/" + status;
        logger.info("Calling Food Delivery Service: PUT {}", url);
        return webClient.put()
                .uri(url)
                .retrieve()
                .bodyToMono(Order.class)
                .doOnError(error -> logger.error("Error updating order {} status to {}: {}", id, status, error.getMessage()));
    }

    /**
     * Guest checkout - create user and order
     * 
     * @param request DTO containing customer info and order data
     * @return Mono of created Order
     */
    public Mono<Order> guestCheckout(GuestCheckoutRequest request) {
        String url = SERVICE_URL + "/guest-checkout";
        logger.info("Calling Food Delivery Service: POST {} (Guest Checkout) - Email: {}", url, request.getCustomerEmail());
        return webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Order.class)
                .doOnSuccess(order -> logger.info("Guest checkout successful - Order ID: {}", order.getId()))
                .doOnError(error -> logger.error("Error processing guest checkout: {}", error.getMessage()));
    }

    /**
     * Get pending orders
     * 
     * @return Flux of pending orders
     */
    public Flux<Order> getPendingOrders() {
        return getOrdersByStatus("PENDING");
    }

    /**
     * Get active orders (pending or confirmed)
     * 
     * @return Flux of active orders
     */
    public Flux<Order> getActiveOrders() {
        String url = SERVICE_URL + "/active";
        logger.info("Calling Food Delivery Service: GET {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Order.class)
                .doOnError(error -> logger.error("Error fetching active orders: {}", error.getMessage()));
    }
}
