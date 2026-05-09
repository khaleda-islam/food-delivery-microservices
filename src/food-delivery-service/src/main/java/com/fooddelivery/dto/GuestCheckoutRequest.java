/*
 * Food Delivery Service - Guest Checkout Request DTO
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 12, 2026
 * 
 * Purpose: Data Transfer Object for guest checkout requests.
 * Provides type safety and validation for guest checkout operations.
 */

package com.fooddelivery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

/**
 * DTO for guest checkout requests.
 * 
 * This DTO combines customer information and order details into a single
 * request object, enabling guest users to place orders without registration.
 * All fields are validated to ensure data integrity.
 */
public class GuestCheckoutRequest {
    
    // ========== Customer Information ==========
    
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String customerPhone;
    
    // ========== Order Information ==========
    
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDTO> items;
    
    // ========== Delivery Information ==========
    
    @NotBlank(message = "Delivery address is required")
    @Size(min = 5, max = 200, message = "Delivery address must be between 5 and 200 characters")
    private String deliveryAddress;
    
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String deliveryCity;
    
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[A-Za-z]\\d[A-Za-z]\\s?\\d[A-Za-z]\\d$", message = "Invalid Canadian postal code format")
    private String deliveryPostalCode;
    
    @NotBlank(message = "Contact number is required")
    private String contactNumber;
    
    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    private String specialInstructions;
    
    // ========== Payment Information ==========
    
    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "CASH|CREDIT_CARD|DEBIT_CARD", message = "Payment method must be CASH, CREDIT_CARD, or DEBIT_CARD")
    private String paymentMethod;
    
    @NotNull(message = "Delivery fee is required")
    @PositiveOrZero(message = "Delivery fee must be zero or positive")
    private Double deliveryFee;
    
    @NotNull(message = "Tax amount is required")
    @PositiveOrZero(message = "Tax amount must be zero or positive")
    private Double taxAmount;
    
    @PositiveOrZero(message = "Discount amount must be zero or positive")
    private Double discountAmount = 0.0;
    
    // ========== Constructors ==========
    
    public GuestCheckoutRequest() {
    }
    
    // ========== Getters and Setters ==========
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public List<OrderItemDTO> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
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
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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
    
    // ========== Nested DTO for Order Items ==========
    
    /**
     * DTO for individual order items within a guest checkout request.
     */
    public static class OrderItemDTO {
        
        @NotBlank(message = "Food ID is required")
        private String foodId;
        
        @NotBlank(message = "Food name is required")
        @Size(min = 2, max = 100, message = "Food name must be between 2 and 100 characters")
        private String foodName;
        
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 99, message = "Quantity cannot exceed 99")
        private Integer quantity;
        
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        private Double price;
        
        @NotNull(message = "Subtotal is required")
        @PositiveOrZero(message = "Subtotal must be zero or positive")
        private Double subtotal;
        
        @Size(max = 200, message = "Special request cannot exceed 200 characters")
        private String specialRequest;
        
        // Constructors
        public OrderItemDTO() {
        }
        
        // Getters and Setters
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
        }
        
        public Double getPrice() {
            return price;
        }
        
        public void setPrice(Double price) {
            this.price = price;
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
    }
}
