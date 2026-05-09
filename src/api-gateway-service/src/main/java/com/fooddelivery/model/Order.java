/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Order DTO for API Gateway Service
 * Note: This is a POJO (Plain Old Java Object) without database annotations
 */
package com.fooddelivery.model;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Data Transfer Object.
 * Used to deserialize responses from Food Delivery Service and send to frontend.
 * NO database annotations - this is NOT a MongoDB entity.
 */
public class Order {

    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItem> items;

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be greater than 0")
    private Double totalPrice;

    @NotNull(message = "Order status is required")
    @Pattern(regexp = "PENDING|CONFIRMED|PREPARING|OUT_FOR_DELIVERY|DELIVERED|CANCELLED", 
             message = "Status must be: PENDING, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, or CANCELLED")
    private String status;

    @NotBlank(message = "Delivery address is required")
    @Size(min = 5, max = 300, message = "Delivery address must be between 5 and 300 characters")
    private String deliveryAddress;

    @Size(max = 50, message = "Delivery city must not exceed 50 characters")
    private String deliveryCity;

    @Size(max = 10, message = "Delivery postal code must not exceed 10 characters")
    private String deliveryPostalCode;

    @Size(max = 15, message = "Contact number must not exceed 15 characters")
    private String contactNumber;

    @Size(max = 500, message = "Special instructions must not exceed 500 characters")
    private String specialInstructions;

    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;

    private LocalDateTime estimatedDeliveryTime;

    private LocalDateTime actualDeliveryTime;

    @Pattern(regexp = "CASH|CREDIT_CARD|DEBIT_CARD|ONLINE|UPI", 
             message = "Payment method must be: CASH, CREDIT_CARD, DEBIT_CARD, ONLINE, or UPI")
    private String paymentMethod;

    @Pattern(regexp = "PENDING|COMPLETED|FAILED|REFUNDED", 
             message = "Payment status must be: PENDING, COMPLETED, FAILED, or REFUNDED")
    private String paymentStatus;

    private String deliveryPartnerId;

    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    private Double deliveryFee;

    @DecimalMin(value = "0.0", message = "Tax amount cannot be negative")
    private Double taxAmount;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private Double discountAmount;

    /**
     * Default constructor
     */
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
        this.paymentStatus = "PENDING";
        this.items = new ArrayList<>();
        this.deliveryFee = 0.0;
        this.taxAmount = 0.0;
        this.discountAmount = 0.0;
    }

    /**
     * Constructor with required fields
     */
    public Order(String userId, String restaurantId, List<OrderItem> items, 
                 Double totalPrice, String deliveryAddress) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items != null ? items : new ArrayList<>();
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
        this.paymentStatus = "PENDING";
        this.deliveryFee = 0.0;
        this.taxAmount = 0.0;
        this.discountAmount = 0.0;
    }

    /**
     * Inner class representing an item in the order
     * This is also a POJO without database annotations
     */
    public static class OrderItem {
        @NotBlank(message = "Food ID is required")
        private String foodId;

        @NotBlank(message = "Food name is required")
        private String foodName;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        private Double price;

        @NotNull(message = "Subtotal is required")
        @DecimalMin(value = "0.01", message = "Subtotal must be greater than 0")
        private Double subtotal;

        private String specialRequest;

        public OrderItem() {
        }

        public OrderItem(String foodId, String foodName, Integer quantity, Double price) {
            this.foodId = foodId;
            this.foodName = foodName;
            this.quantity = quantity;
            this.price = price;
            this.subtotal = quantity * price;
        }

        // Getters and Setters for OrderItem
        public String getFoodId() {
            return foodId;
        }

        public void setFoodId(String foodId) {
            this.foodId = foodId;
        }

        public String getFoodName() {
            return foodName;
        }

        public void setFoodName(String foodName) {
            this.foodName = foodName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
            if (this.price != null) {
                this.subtotal = quantity * this.price;
            }
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
            if (this.quantity != null) {
                this.subtotal = this.quantity * price;
            }
        }

        public Double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(Double subtotal) {
            this.subtotal = subtotal;
        }

        public String getSpecialRequest() {
            return specialRequest;
        }

        public void setSpecialRequest(String specialRequest) {
            this.specialRequest = specialRequest;
        }

        @Override
        public String toString() {
            return "OrderItem{" +
                    "foodId='" + foodId + '\'' +
                    ", foodName='" + foodName + '\'' +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    ", subtotal=" + subtotal +
                    ", specialRequest='" + specialRequest + '\'' +
                    '}';
        }
    }

    // Getters and Setters for Order
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public String getDeliveryPostalCode() {
        return deliveryPostalCode;
    }

    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public LocalDateTime getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDeliveryPartnerId() {
        return deliveryPartnerId;
    }

    public void setDeliveryPartnerId(String deliveryPartnerId) {
        this.deliveryPartnerId = deliveryPartnerId;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", items=" + items +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", deliveryCity='" + deliveryCity + '\'' +
                ", deliveryPostalCode='" + deliveryPostalCode + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", specialInstructions='" + specialInstructions + '\'' +
                ", orderDate=" + orderDate +
                ", estimatedDeliveryTime=" + estimatedDeliveryTime +
                ", actualDeliveryTime=" + actualDeliveryTime +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", deliveryPartnerId='" + deliveryPartnerId + '\'' +
                ", deliveryFee=" + deliveryFee +
                ", taxAmount=" + taxAmount +
                ", discountAmount=" + discountAmount +
                '}';
    }
}
