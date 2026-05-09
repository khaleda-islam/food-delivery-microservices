/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Food DTO for API Gateway Service
 * Note: This is a POJO (Plain Old Java Object) without database annotations
 */
package com.fooddelivery.model;

import jakarta.validation.constraints.*;

/**
 * Food Data Transfer Object.
 * Used to deserialize responses from Food Delivery Service and send to frontend.
 * NO database annotations - this is NOT a MongoDB entity.
 */
public class Food {

    private String id;

    @NotBlank(message = "Food name is required")
    @Size(min = 2, max = 100, message = "Food name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "9999.99", message = "Price cannot exceed 9999.99")
    private Double price;

    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category;

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    private String imageUrl;

    private Boolean isAvailable;

    @Min(value = 0, message = "Preparation time must be at least 0 minutes")
    @Max(value = 300, message = "Preparation time cannot exceed 300 minutes")
    private Integer preparationTimeMinutes;

    private Boolean isVegetarian;

    private Boolean isVegan;

    /**
     * Default constructor
     */
    public Food() {
    }

    /**
     * Constructor with required fields
     */
    public Food(String name, Double price, String category, String restaurantId) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.restaurantId = restaurantId;
        this.isAvailable = true;
        this.isVegetarian = false;
        this.isVegan = false;
    }

    /**
     * Constructor with all fields
     */
    public Food(String id, String name, String description, Double price, String category,
                String restaurantId, String imageUrl, Boolean isAvailable,
                Integer preparationTimeMinutes, Boolean isVegetarian, Boolean isVegan) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.restaurantId = restaurantId;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.isVegetarian = isVegetarian;
        this.isVegan = isVegan;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(Integer preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public Boolean getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public Boolean getIsVegan() {
        return isVegan;
    }

    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }

    @Override
    public String toString() {
        return "Food{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isAvailable=" + isAvailable +
                ", preparationTimeMinutes=" + preparationTimeMinutes +
                ", isVegetarian=" + isVegetarian +
                ", isVegan=" + isVegan +
                '}';
    }
}
