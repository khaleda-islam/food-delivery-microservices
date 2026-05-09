/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Reactive repository for Order entity with custom query methods
 */
package com.fooddelivery.repository;

import com.fooddelivery.model.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

/**
 * Reactive repository for Order entity.
 * Provides CRUD operations and custom query methods using reactive types.
 */
@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    /**
     * Find all orders for a specific user
     * @param userId the user's ID
     * @return Flux of orders belonging to the specified user
     */
    Flux<Order> findByUserId(String userId);

    /**
     * Find all orders from a specific restaurant
     * @param restaurantId the restaurant's ID
     * @return Flux of orders from the specified restaurant
     */
    Flux<Order> findByRestaurantId(String restaurantId);

    /**
     * Find orders by status
     * @param status the order status (PENDING, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)
     * @return Flux of orders with the specified status
     */
    Flux<Order> findByStatus(String status);

    /**
     * Find orders by user and status
     * @param userId the user's ID
     * @param status the order status
     * @return Flux of orders matching both criteria
     */
    Flux<Order> findByUserIdAndStatus(String userId, String status);

    /**
     * Find orders by restaurant and status
     * @param restaurantId the restaurant's ID
     * @param status the order status
     * @return Flux of orders matching both criteria
     */
    Flux<Order> findByRestaurantIdAndStatus(String restaurantId, String status);

    /**
     * Find orders by payment status
     * @param paymentStatus the payment status (PENDING, COMPLETED, FAILED, REFUNDED)
     * @return Flux of orders with the specified payment status
     */
    Flux<Order> findByPaymentStatus(String paymentStatus);

    /**
     * Find orders by payment method
     * @param paymentMethod the payment method (CASH, CREDIT_CARD, DEBIT_CARD, ONLINE, UPI)
     * @return Flux of orders with the specified payment method
     */
    Flux<Order> findByPaymentMethod(String paymentMethod);

    /**
     * Find orders assigned to a delivery partner
     * @param deliveryPartnerId the delivery partner's ID
     * @return Flux of orders assigned to the specified delivery partner
     */
    Flux<Order> findByDeliveryPartnerId(String deliveryPartnerId);

    /**
     * Find orders by delivery partner and status
     * @param deliveryPartnerId the delivery partner's ID
     * @param status the order status
     * @return Flux of orders matching both criteria
     */
    Flux<Order> findByDeliveryPartnerIdAndStatus(String deliveryPartnerId, String status);

    /**
     * Find orders placed after a specific date/time
     * @param orderDate the date/time threshold
     * @return Flux of orders placed after the specified date/time
     */
    Flux<Order> findByOrderDateAfter(LocalDateTime orderDate);

    /**
     * Find orders placed between two dates
     * @param startDate start date/time
     * @param endDate end date/time
     * @return Flux of orders placed within the specified date range
     */
    Flux<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find orders by user within a date range
     * @param userId the user's ID
     * @param startDate start date/time
     * @param endDate end date/time
     * @return Flux of orders matching all criteria
     */
    Flux<Order> findByUserIdAndOrderDateBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find orders by restaurant within a date range
     * @param restaurantId the restaurant's ID
     * @param startDate start date/time
     * @param endDate end date/time
     * @return Flux of orders matching all criteria
     */
    Flux<Order> findByRestaurantIdAndOrderDateBetween(String restaurantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find orders by delivery city
     * @param deliveryCity the delivery city
     * @return Flux of orders for the specified city
     */
    Flux<Order> findByDeliveryCity(String deliveryCity);

    /**
     * Find orders with total price greater than specified amount
     * @param totalPrice minimum total price
     * @return Flux of orders with total price greater than the specified amount
     */
    Flux<Order> findByTotalPriceGreaterThan(Double totalPrice);

    /**
     * Find orders with total price between two amounts
     * @param minPrice minimum total price
     * @param maxPrice maximum total price
     * @return Flux of orders within the specified price range
     */
    Flux<Order> findByTotalPriceBetween(Double minPrice, Double maxPrice);

    /**
     * Count orders by user ID
     * @param userId the user's ID
     * @return Mono of Long representing the count
     */
    Mono<Long> countByUserId(String userId);

    /**
     * Count orders by restaurant ID
     * @param restaurantId the restaurant's ID
     * @return Mono of Long representing the count
     */
    Mono<Long> countByRestaurantId(String restaurantId);

    /**
     * Count orders by status
     * @param status the order status
     * @return Mono of Long representing the count
     */
    Mono<Long> countByStatus(String status);

    /**
     * Count orders by user and status
     * @param userId the user's ID
     * @param status the order status
     * @return Mono of Long representing the count
     */
    Mono<Long> countByUserIdAndStatus(String userId, String status);

    /**
     * Find the most recent orders for a user (sorted by order date descending)
     * @param userId the user's ID
     * @return Flux of orders sorted by date (newest first)
     */
    Flux<Order> findByUserIdOrderByOrderDateDesc(String userId);

    /**
     * Find the most recent orders for a restaurant (sorted by order date descending)
     * @param restaurantId the restaurant's ID
     * @return Flux of orders sorted by date (newest first)
     */
    Flux<Order> findByRestaurantIdOrderByOrderDateDesc(String restaurantId);
}
