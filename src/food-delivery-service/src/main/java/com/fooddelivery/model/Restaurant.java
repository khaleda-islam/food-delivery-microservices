/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * Description: Restaurant entity representing a restaurant in the food delivery system
 */
package com.fooddelivery.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

/**
 * Restaurant entity mapped to "restaurants" MongoDB collection.
 * Represents a restaurant that offers food items for delivery.
 */
@Document(collection = "restaurants")
public class Restaurant {

    @Id
    private String id;

    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City name must be between 2 and 50 characters")
    private String city;

    @NotBlank(message = "Cuisine type is required")
    @Size(min = 2, max = 50, message = "Cuisine type must be between 2 and 50 characters")
    private String cuisineType;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double rating;

    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phoneNumber;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    private Boolean isActive = true;

    /**
     * Default constructor
     */
    public Restaurant() {
    }

    /**
     * Constructor with all required fields
     */
    public Restaurant(String name, String city, String cuisineType, Double rating) {
        this.name = name;
        this.city = city;
        this.cuisineType = cuisineType;
        this.rating = rating;
        this.isActive = true;
    }

    /**
     * Constructor with all fields
     */
    public Restaurant(String id, String name, String city, String cuisineType, Double rating, 
                      String phoneNumber, String address, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.cuisineType = cuisineType;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isActive = isActive;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", cuisineType='" + cuisineType + '\'' +
                ", rating=" + rating +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
