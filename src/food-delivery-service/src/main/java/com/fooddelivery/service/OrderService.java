/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Service layer for Order entity with complex business logic and error handling
 */
package com.fooddelivery.service;

import com.fooddelivery.dto.GuestCheckoutRequest;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for Order entity.
 * Handles complex order management, order placement, status tracking, and business logic.
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository repository;

    @Autowired
    private UserService userService;

    private static final double TAX_RATE = 0.13; // 13% tax
    private static final double DELIVERY_FEE_BASE = 5.00; // Base delivery fee

    /**
     * Find all orders
     * @return Flux of all orders
     */
    public Flux<Order> findAll() {
        logger.info("Fetching all orders");
        return repository.findAll()
                .doOnComplete(() -> logger.info("Successfully fetched all orders"))
                .doOnError(error -> logger.error("Error fetching all orders", error));
    }

    /**
     * Find order by ID
     * @param id the order ID
     * @return Mono of Order if found, empty Mono otherwise
     */
    public Mono<Order> findById(String id) {
        logger.info("Fetching order with ID: {}", id);
        return repository.findById(id)
                .doOnSuccess(order -> {
                    if (order != null) {
                        logger.info("Found order: {} for user: {}", order.getId(), order.getUserId());
                    } else {
                        logger.warn("Order not found with ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error fetching order with ID: {}", id, error))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("No order found with ID: {}", id);
                    return Mono.empty();
                }));
    }

    /**
     * Place a new order with automatic calculations
     * @param order the order to place
     * @return Mono of created Order
     */
    public Mono<Order> placeOrder(Order order) {
        logger.info("Placing new order for user: {} at restaurant: {}", order.getUserId(), order.getRestaurantId());
        
        return Mono.fromCallable(() -> {
            // Calculate subtotal from items
            double subtotal = order.getItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            
            // Calculate tax
            double taxAmount = subtotal * TAX_RATE;
            order.setTaxAmount(taxAmount);
            
            // Set delivery fee (could be dynamic based on distance in real scenario)
            if (order.getDeliveryFee() == null || order.getDeliveryFee() == 0) {
                order.setDeliveryFee(DELIVERY_FEE_BASE);
            }
            
            // Apply discount if any
            if (order.getDiscountAmount() == null) {
                order.setDiscountAmount(0.0);
            }
            
            // Calculate total price
            double totalPrice = subtotal + taxAmount + order.getDeliveryFee() - order.getDiscountAmount();
            order.setTotalPrice(totalPrice);
            
            // Set order metadata
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING");
            order.setPaymentStatus("PENDING");
            
            // Set estimated delivery time (e.g., 45 minutes from now)
            order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(45));
            
            return order;
        })
        .flatMap(preparedOrder -> repository.save(preparedOrder))
        .doOnSuccess(savedOrder -> logger.info("Successfully placed order: {} with total: ${}", 
                savedOrder.getId(), savedOrder.getTotalPrice()))
        .doOnError(error -> logger.error("Error placing order", error))
        .onErrorResume(error -> {
            logger.error("Failed to place order", error);
            return Mono.error(new RuntimeException("Failed to place order: " + error.getMessage()));
        });
    }

    /**
     * Process guest checkout - create user if doesn't exist, then create order
     * @param request DTO containing user info and order data
     * @return Mono of created Order
     */
    public Mono<Order> processGuestCheckout(GuestCheckoutRequest request) {
        logger.info("Processing guest checkout for email: {}", request.getCustomerEmail());
        
        // Step 1: Check if user exists by email, create if not
        return userService.findByEmail(request.getCustomerEmail())
            .switchIfEmpty(
                // User doesn't exist - create new user
                Mono.defer(() -> {
                    logger.info("Creating new guest user: {}", request.getCustomerEmail());
                    User newUser = new User();
                    newUser.setName(request.getCustomerName());
                    newUser.setEmail(request.getCustomerEmail());
                    newUser.setPhoneNumber(request.getCustomerPhone());
                    newUser.setAddress(request.getDeliveryAddress());
                    newUser.setCity(request.getDeliveryCity());
                    newUser.setPostalCode(request.getDeliveryPostalCode());
                    newUser.setPassword("GUEST_" + System.currentTimeMillis()); // Auto-generated password
                    newUser.setRole("CUSTOMER");
                    newUser.setIsActive(true);
                    
                    return userService.save(newUser);
                })
            )
            .flatMap(user -> {
                // Step 2: Create order with the user ID
                logger.info("Creating order for user: {}", user.getId());
                
                // Build Order object from DTO
                Order order = new Order();
                order.setUserId(user.getId());
                order.setRestaurantId(request.getRestaurantId());
                order.setDeliveryAddress(request.getDeliveryAddress());
                order.setDeliveryCity(request.getDeliveryCity());
                order.setDeliveryPostalCode(request.getDeliveryPostalCode());
                order.setContactNumber(request.getContactNumber());
                order.setSpecialInstructions(request.getSpecialInstructions());
                order.setPaymentMethod(request.getPaymentMethod());
                order.setDeliveryFee(request.getDeliveryFee());
                order.setTaxAmount(request.getTaxAmount());
                order.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0);
                
                // Convert DTO items to OrderItem objects
                List<Order.OrderItem> orderItems = request.getItems().stream()
                    .map(itemDTO -> {
                        Order.OrderItem item = new Order.OrderItem();
                        item.setFoodId(itemDTO.getFoodId());
                        item.setFoodName(itemDTO.getFoodName());
                        item.setQuantity(itemDTO.getQuantity());
                        item.setPrice(itemDTO.getPrice());
                        item.setSubtotal(itemDTO.getSubtotal());
                        item.setSpecialRequest(itemDTO.getSpecialRequest() != null ? itemDTO.getSpecialRequest() : "");
                        return item;
                    })
                    .collect(Collectors.toList());
                
                order.setItems(orderItems);
                
                // Use existing placeOrder method to handle calculations and save
                return placeOrder(order);
            })
            .doOnSuccess(order -> logger.info("Guest checkout completed successfully. Order ID: {}", order.getId()))
            .doOnError(error -> logger.error("Error processing guest checkout: {}", error.getMessage(), error));
    }

    /**
     * Update order status
     * @param id the order ID
     * @param newStatus the new order status
     * @return Mono of updated Order
     */
    public Mono<Order> updateOrderStatus(String id, String newStatus) {
        logger.info("Updating order {} status to: {}", id, newStatus);
        
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found with ID: " + id)))
                .flatMap(order -> {
                    String oldStatus = order.getStatus();
                    order.setStatus(newStatus);
                    
                    // Set actual delivery time when order is delivered
                    if ("DELIVERED".equals(newStatus) && order.getActualDeliveryTime() == null) {
                        order.setActualDeliveryTime(LocalDateTime.now());
                        logger.info("Order {} delivered at: {}", id, order.getActualDeliveryTime());
                    }
                    
                    return repository.save(order)
                            .doOnSuccess(updated -> logger.info("Order {} status changed from {} to {}", 
                                    id, oldStatus, newStatus));
                })
                .doOnError(error -> logger.error("Error updating order status", error));
    }

    /**
     * Update payment status
     * @param id the order ID
     * @param paymentStatus the new payment status
     * @return Mono of updated Order
     */
    public Mono<Order> updatePaymentStatus(String id, String paymentStatus) {
        logger.info("Updating order {} payment status to: {}", id, paymentStatus);
        
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found with ID: " + id)))
                .flatMap(order -> {
                    order.setPaymentStatus(paymentStatus);
                    
                    // Auto-confirm order when payment is completed
                    if ("COMPLETED".equals(paymentStatus) && "PENDING".equals(order.getStatus())) {
                        order.setStatus("CONFIRMED");
                        logger.info("Order {} auto-confirmed after payment completion", id);
                    }
                    
                    return repository.save(order);
                })
                .doOnSuccess(updated -> logger.info("Payment status updated for order: {}", id))
                .doOnError(error -> logger.error("Error updating payment status", error));
    }

    /**
     * Cancel an order
     * @param id the order ID
     * @param reason cancellation reason
     * @return Mono of cancelled Order
     */
    public Mono<Order> cancelOrder(String id, String reason) {
        logger.info("Cancelling order: {} - Reason: {}", id, reason);
        
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found with ID: " + id)))
                .flatMap(order -> {
                    // Check if order can be cancelled
                    if ("DELIVERED".equals(order.getStatus())) {
                        return Mono.error(new RuntimeException("Cannot cancel delivered order"));
                    }
                    if ("CANCELLED".equals(order.getStatus())) {
                        return Mono.error(new RuntimeException("Order is already cancelled"));
                    }
                    
                    order.setStatus("CANCELLED");
                    if (reason != null) {
                        order.setSpecialInstructions(
                                (order.getSpecialInstructions() != null ? order.getSpecialInstructions() + " | " : "") 
                                + "Cancellation Reason: " + reason
                        );
                    }
                    
                    return repository.save(order);
                })
                .doOnSuccess(cancelled -> logger.info("Order {} successfully cancelled", id))
                .doOnError(error -> logger.error("Error cancelling order: {}", id, error));
    }

    /**
     * Assign delivery partner to order
     * @param orderId the order ID
     * @param deliveryPartnerId the delivery partner's ID
     * @return Mono of updated Order
     */
    public Mono<Order> assignDeliveryPartner(String orderId, String deliveryPartnerId) {
        logger.info("Assigning delivery partner {} to order {}", deliveryPartnerId, orderId);
        
        return repository.findById(orderId)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found with ID: " + orderId)))
                .flatMap(order -> {
                    order.setDeliveryPartnerId(deliveryPartnerId);
                    
                    // Update status to OUT_FOR_DELIVERY if preparing
                    if ("PREPARING".equals(order.getStatus())) {
                        order.setStatus("OUT_FOR_DELIVERY");
                    }
                    
                    return repository.save(order);
                })
                .doOnSuccess(updated -> logger.info("Delivery partner assigned to order: {}", orderId))
                .doOnError(error -> logger.error("Error assigning delivery partner", error));
    }

    /**
     * Find orders by user ID
     * @param userId the user's ID
     * @return Flux of user's orders
     */
    public Flux<Order> findOrdersByUser(String userId) {
        logger.info("Fetching orders for user: {}", userId);
        return repository.findByUserId(userId)
                .doOnComplete(() -> logger.info("Successfully fetched orders for user: {}", userId))
                .doOnError(error -> logger.error("Error fetching orders for user", error));
    }

    /**
     * Find recent orders for a user (sorted by date)
     * @param userId the user's ID
     * @return Flux of recent orders
     */
    public Flux<Order> findRecentOrdersByUser(String userId) {
        logger.info("Fetching recent orders for user: {}", userId);
        return repository.findByUserIdOrderByOrderDateDesc(userId)
                .doOnComplete(() -> logger.info("Successfully fetched recent orders for user: {}", userId))
                .doOnError(error -> logger.error("Error fetching recent orders", error));
    }

    /**
     * Find orders by restaurant ID
     * @param restaurantId the restaurant's ID
     * @return Flux of restaurant's orders
     */
    public Flux<Order> findOrdersByRestaurant(String restaurantId) {
        logger.info("Fetching orders for restaurant: {}", restaurantId);
        return repository.findByRestaurantId(restaurantId)
                .doOnComplete(() -> logger.info("Successfully fetched orders for restaurant: {}", restaurantId))
                .doOnError(error -> logger.error("Error fetching orders for restaurant", error));
    }

    /**
     * Find recent orders for a restaurant (sorted by date)
     * @param restaurantId the restaurant's ID
     * @return Flux of recent orders
     */
    public Flux<Order> findRecentOrdersByRestaurant(String restaurantId) {
        logger.info("Fetching recent orders for restaurant: {}", restaurantId);
        return repository.findByRestaurantIdOrderByOrderDateDesc(restaurantId)
                .doOnComplete(() -> logger.info("Successfully fetched recent orders for restaurant: {}", restaurantId))
                .doOnError(error -> logger.error("Error fetching recent orders for restaurant", error));
    }

    /**
     * Find orders by status
     * @param status the order status
     * @return Flux of orders with the specified status
     */
    public Flux<Order> findOrdersByStatus(String status) {
        logger.info("Fetching orders with status: {}", status);
        return repository.findByStatus(status)
                .doOnComplete(() -> logger.info("Successfully fetched orders with status: {}", status))
                .doOnError(error -> logger.error("Error fetching orders by status", error));
    }

    /**
     * Find orders by delivery partner ID
     * @param deliveryPartnerId the delivery partner's ID
     * @return Flux of orders assigned to the delivery partner
     */
    public Flux<Order> findOrdersByDeliveryPartner(String deliveryPartnerId) {
        logger.info("Fetching orders for delivery partner: {}", deliveryPartnerId);
        return repository.findByDeliveryPartnerId(deliveryPartnerId)
                .doOnComplete(() -> logger.info("Successfully fetched orders for delivery partner: {}", deliveryPartnerId))
                .doOnError(error -> logger.error("Error fetching orders for delivery partner", error));
    }

    /**
     * Find active orders for delivery partner
     * @param deliveryPartnerId the delivery partner's ID
     * @return Flux of active orders for the delivery partner
     */
    public Flux<Order> findActiveOrdersForDeliveryPartner(String deliveryPartnerId) {
        logger.info("Fetching active orders for delivery partner: {}", deliveryPartnerId);
        return repository.findByDeliveryPartnerIdAndStatus(deliveryPartnerId, "OUT_FOR_DELIVERY")
                .doOnComplete(() -> logger.info("Successfully fetched active orders for delivery partner"))
                .doOnError(error -> logger.error("Error fetching active orders", error));
    }

    /**
     * Find orders within a date range
     * @param startDate start date
     * @param endDate end date
     * @return Flux of orders within the date range
     */
    public Flux<Order> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Fetching orders between {} and {}", startDate, endDate);
        return repository.findByOrderDateBetween(startDate, endDate)
                .doOnComplete(() -> logger.info("Successfully fetched orders in date range"))
                .doOnError(error -> logger.error("Error fetching orders by date range", error));
    }

    /**
     * Find orders by user within date range
     * @param userId the user's ID
     * @param startDate start date
     * @param endDate end date
     * @return Flux of orders
     */
    public Flux<Order> findUserOrdersByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Fetching orders for user {} between {} and {}", userId, startDate, endDate);
        return repository.findByUserIdAndOrderDateBetween(userId, startDate, endDate)
                .doOnComplete(() -> logger.info("Successfully fetched user orders in date range"))
                .doOnError(error -> logger.error("Error fetching user orders by date range", error));
    }

    /**
     * Count orders by user
     * @param userId the user's ID
     * @return Mono of Long representing the count
     */
    public Mono<Long> countOrdersByUser(String userId) {
        logger.info("Counting orders for user: {}", userId);
        return repository.countByUserId(userId)
                .doOnSuccess(count -> logger.info("User {} has {} orders", userId, count))
                .doOnError(error -> logger.error("Error counting orders for user", error));
    }

    /**
     * Count orders by restaurant
     * @param restaurantId the restaurant's ID
     * @return Mono of Long representing the count
     */
    public Mono<Long> countOrdersByRestaurant(String restaurantId) {
        logger.info("Counting orders for restaurant: {}", restaurantId);
        return repository.countByRestaurantId(restaurantId)
                .doOnSuccess(count -> logger.info("Restaurant {} has {} orders", restaurantId, count))
                .doOnError(error -> logger.error("Error counting orders for restaurant", error));
    }

    /**
     * Count orders by status
     * @param status the order status
     * @return Mono of Long representing the count
     */
    public Mono<Long> countOrdersByStatus(String status) {
        logger.info("Counting orders with status: {}", status);
        return repository.countByStatus(status)
                .doOnSuccess(count -> logger.info("Found {} orders with status: {}", count, status))
                .doOnError(error -> logger.error("Error counting orders by status", error));
    }

    /**
     * Delete order by ID
     * @param id the order ID
     * @return Mono of Void when deletion is complete
     */
    public Mono<Void> deleteById(String id) {
        logger.info("Deleting order with ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("Cannot delete: Order not found with ID: {}", id);
                    return Mono.error(new RuntimeException("Order not found with ID: " + id));
                }))
                .flatMap(order -> {
                    logger.info("Deleting order: {} for user: {}", order.getId(), order.getUserId());
                    return repository.deleteById(id);
                })
                .doOnSuccess(unused -> logger.info("Successfully deleted order with ID: {}", id))
                .doOnError(error -> logger.error("Error deleting order with ID: {}", id, error));
    }

    /**
     * Calculate order statistics for a restaurant
     * @param restaurantId the restaurant's ID
     * @param startDate start date for statistics
     * @param endDate end date for statistics
     * @return Mono of order statistics summary
     */
    public Mono<String> getRestaurantOrderStatistics(String restaurantId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Calculating order statistics for restaurant: {}", restaurantId);
        
        return repository.findByRestaurantIdAndOrderDateBetween(restaurantId, startDate, endDate)
                .collectList()
                .map(orders -> {
                    long totalOrders = orders.size();
                    double totalRevenue = orders.stream()
                            .mapToDouble(Order::getTotalPrice)
                            .sum();
                    long completedOrders = orders.stream()
                            .filter(o -> "DELIVERED".equals(o.getStatus()))
                            .count();
                    
                    String stats = String.format(
                            "Restaurant Statistics - Total Orders: %d, Completed: %d, Total Revenue: $%.2f",
                            totalOrders, completedOrders, totalRevenue
                    );
                    
                    logger.info(stats);
                    return stats;
                })
                .doOnError(error -> logger.error("Error calculating statistics", error));
    }
}
