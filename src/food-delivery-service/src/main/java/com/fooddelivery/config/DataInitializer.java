/*
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Course: Enterprise Application Development
 * Assignment: Online Food Delivery System - Microservice Architecture
 */

package com.fooddelivery.config;

import com.fooddelivery.model.Food;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.FoodRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Data Initializer Configuration
 * Loads seed data into MongoDB when the application starts if database is empty
 */
@Configuration
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Bean
    CommandLineRunner initDatabase(RestaurantRepository restaurantRepository,
                                   FoodRepository foodRepository,
                                   UserRepository userRepository,
                                   OrderRepository orderRepository) {
        
        return args -> {
            logger.info("Checking database for existing data...");
            
            // Check if restaurants collection is empty
            Long restaurantCount = restaurantRepository.count().block();
            
            if (restaurantCount != null && restaurantCount > 0) {
                logger.info("Database already contains data. Skipping seed data initialization.");
                return;
            }
            
            logger.info("Database is empty. Loading seed data...");
            
            // ========== SEED RESTAURANTS ==========
            Restaurant restaurant1 = new Restaurant();
            restaurant1.setName("Pizza Palace");
            restaurant1.setCity("Toronto");
            restaurant1.setCuisineType("Italian");
            restaurant1.setRating(4.5);
            restaurant1.setPhoneNumber("416-555-1234");
            restaurant1.setAddress("123 King Street West, Toronto, ON M5H 1A1");
            restaurant1.setIsActive(true);
            
            Restaurant restaurant2 = new Restaurant();
            restaurant2.setName("Sushi Express");
            restaurant2.setCity("Toronto");
            restaurant2.setCuisineType("Japanese");
            restaurant2.setRating(4.7);
            restaurant2.setPhoneNumber("416-555-5678");
            restaurant2.setAddress("456 Queen Street West, Toronto, ON M5V 2A8");
            restaurant2.setIsActive(true);
            
            Restaurant restaurant3 = new Restaurant();
            restaurant3.setName("Burger Junction");
            restaurant3.setCity("Mississauga");
            restaurant3.setCuisineType("American");
            restaurant3.setRating(4.2);
            restaurant3.setPhoneNumber("905-555-9012");
            restaurant3.setAddress("789 Dundas Street, Mississauga, ON L5B 1H8");
            restaurant3.setIsActive(true);
            
            Restaurant restaurant4 = new Restaurant();
            restaurant4.setName("Tandoori Nights");
            restaurant4.setCity("Brampton");
            restaurant4.setCuisineType("Indian");
            restaurant4.setRating(4.6);
            restaurant4.setPhoneNumber("905-555-3456");
            restaurant4.setAddress("321 Main Street North, Brampton, ON L6X 1N2");
            restaurant4.setIsActive(true);
            
            List<Restaurant> restaurants = restaurantRepository
                    .saveAll(Arrays.asList(restaurant1, restaurant2, restaurant3, restaurant4))
                    .collectList()
                    .block();
            
            logger.info("Loaded {} restaurants", restaurants != null ? restaurants.size() : 0);
            
            // ========== SEED FOOD ITEMS ==========
            if (restaurants != null && !restaurants.isEmpty()) {
                String pizzaPalaceId = restaurants.get(0).getId();
                String sushiExpressId = restaurants.get(1).getId();
                String burgerJunctionId = restaurants.get(2).getId();
                String tandooriNightsId = restaurants.get(3).getId();
                
                Food food1 = new Food();
                food1.setName("Margherita Pizza");
                food1.setDescription("Classic pizza with fresh mozzarella, tomatoes, and basil");
                food1.setPrice(12.99);
                food1.setCategory("Pizza");
                food1.setRestaurantId(pizzaPalaceId);
                food1.setImageUrl("https://example.com/images/margherita.jpg");
                food1.setIsAvailable(true);
                food1.setPreparationTimeMinutes(25);
                food1.setIsVegetarian(true);
                food1.setIsVegan(false);
                
                Food food2 = new Food();
                food2.setName("Pepperoni Pizza");
                food2.setDescription("Traditional pizza with pepperoni and cheese");
                food2.setPrice(14.99);
                food2.setCategory("Pizza");
                food2.setRestaurantId(pizzaPalaceId);
                food2.setImageUrl("https://example.com/images/pepperoni.jpg");
                food2.setIsAvailable(true);
                food2.setPreparationTimeMinutes(25);
                food2.setIsVegetarian(false);
                food2.setIsVegan(false);
                
                Food food3 = new Food();
                food3.setName("California Roll");
                food3.setDescription("Sushi roll with crab, avocado, and cucumber");
                food3.setPrice(8.99);
                food3.setCategory("Sushi");
                food3.setRestaurantId(sushiExpressId);
                food3.setImageUrl("https://example.com/images/california-roll.jpg");
                food3.setIsAvailable(true);
                food3.setPreparationTimeMinutes(15);
                food3.setIsVegetarian(false);
                food3.setIsVegan(false);
                
                Food food4 = new Food();
                food4.setName("Salmon Nigiri");
                food4.setDescription("Fresh salmon over sushi rice");
                food4.setPrice(6.99);
                food4.setCategory("Sushi");
                food4.setRestaurantId(sushiExpressId);
                food4.setImageUrl("https://example.com/images/salmon-nigiri.jpg");
                food4.setIsAvailable(true);
                food4.setPreparationTimeMinutes(10);
                food4.setIsVegetarian(false);
                food4.setIsVegan(false);
                
                Food food5 = new Food();
                food5.setName("Classic Cheeseburger");
                food5.setDescription("Beef patty with cheese, lettuce, tomato, and special sauce");
                food5.setPrice(10.99);
                food5.setCategory("Burgers");
                food5.setRestaurantId(burgerJunctionId);
                food5.setImageUrl("https://example.com/images/cheeseburger.jpg");
                food5.setIsAvailable(true);
                food5.setPreparationTimeMinutes(20);
                food5.setIsVegetarian(false);
                food5.setIsVegan(false);
                
                Food food6 = new Food();
                food6.setName("Veggie Burger");
                food6.setDescription("Plant-based patty with fresh vegetables");
                food6.setPrice(11.99);
                food6.setCategory("Burgers");
                food6.setRestaurantId(burgerJunctionId);
                food6.setImageUrl("https://example.com/images/veggie-burger.jpg");
                food6.setIsAvailable(true);
                food6.setPreparationTimeMinutes(20);
                food6.setIsVegetarian(true);
                food6.setIsVegan(true);
                
                Food food7 = new Food();
                food7.setName("Chicken Tikka Masala");
                food7.setDescription("Tender chicken in creamy tomato curry sauce");
                food7.setPrice(15.99);
                food7.setCategory("Curry");
                food7.setRestaurantId(tandooriNightsId);
                food7.setImageUrl("https://example.com/images/tikka-masala.jpg");
                food7.setIsAvailable(true);
                food7.setPreparationTimeMinutes(30);
                food7.setIsVegetarian(false);
                food7.setIsVegan(false);
                
                Food food8 = new Food();
                food8.setName("Paneer Butter Masala");
                food8.setDescription("Cottage cheese in rich creamy gravy");
                food8.setPrice(13.99);
                food8.setCategory("Curry");
                food8.setRestaurantId(tandooriNightsId);
                food8.setImageUrl("https://example.com/images/paneer-masala.jpg");
                food8.setIsAvailable(true);
                food8.setPreparationTimeMinutes(25);
                food8.setIsVegetarian(true);
                food8.setIsVegan(false);
                
                Food food9 = new Food();
                food9.setName("Garlic Naan");
                food9.setDescription("Fresh baked flatbread with garlic and butter");
                food9.setPrice(3.99);
                food9.setCategory("Bread");
                food9.setRestaurantId(tandooriNightsId);
                food9.setImageUrl("https://example.com/images/garlic-naan.jpg");
                food9.setIsAvailable(true);
                food9.setPreparationTimeMinutes(10);
                food9.setIsVegetarian(true);
                food9.setIsVegan(false);
                
                Food food10 = new Food();
                food10.setName("Caesar Salad");
                food10.setDescription("Crisp romaine lettuce with Caesar dressing and croutons");
                food10.setPrice(7.99);
                food10.setCategory("Salad");
                food10.setRestaurantId(pizzaPalaceId);
                food10.setImageUrl("https://example.com/images/caesar-salad.jpg");
                food10.setIsAvailable(true);
                food10.setPreparationTimeMinutes(10);
                food10.setIsVegetarian(true);
                food10.setIsVegan(false);
                
                List<Food> foods = foodRepository
                        .saveAll(Arrays.asList(food1, food2, food3, food4, food5, 
                                              food6, food7, food8, food9, food10))
                        .collectList()
                        .block();
                
                logger.info("Loaded {} food items", foods != null ? foods.size() : 0);
            }
            
            // ========== SEED USERS ==========
            User user1 = new User();
            user1.setName("John Doe");
            user1.setEmail("john.doe@example.com");
            user1.setPassword("password123"); // In production, this should be hashed
            user1.setPhoneNumber("416-555-0001");
            user1.setAddress("100 Main Street");
            user1.setCity("Toronto");
            user1.setPostalCode("M5H 2Y2");
            user1.setRole("CUSTOMER");
            user1.setIsActive(true);
            user1.setCreatedAt(LocalDateTime.now());
            user1.setUpdatedAt(LocalDateTime.now());
            
            User user2 = new User();
            user2.setName("Jane Smith");
            user2.setEmail("jane.smith@example.com");
            user2.setPassword("password123");
            user2.setPhoneNumber("905-555-0002");
            user2.setAddress("200 Oak Avenue");
            user2.setCity("Mississauga");
            user2.setPostalCode("L5B 3C1");
            user2.setRole("CUSTOMER");
            user2.setIsActive(true);
            user2.setCreatedAt(LocalDateTime.now());
            user2.setUpdatedAt(LocalDateTime.now());
            
            User user3 = new User();
            user3.setName("Admin User");
            user3.setEmail("admin@fooddelivery.com");
            user3.setPassword("admin123");
            user3.setPhoneNumber("416-555-0000");
            user3.setAddress("300 Admin Plaza");
            user3.setCity("Toronto");
            user3.setPostalCode("M5K 1E7");
            user3.setRole("ADMIN");
            user3.setIsActive(true);
            user3.setCreatedAt(LocalDateTime.now());
            user3.setUpdatedAt(LocalDateTime.now());
            
            User user4 = new User();
            user4.setName("Mike Driver");
            user4.setEmail("mike.driver@example.com");
            user4.setPassword("password123");
            user4.setPhoneNumber("416-555-0003");
            user4.setAddress("400 Delivery Lane");
            user4.setCity("Toronto");
            user4.setPostalCode("M4C 1B5");
            user4.setRole("DELIVERY_PARTNER");
            user4.setIsActive(true);
            user4.setCreatedAt(LocalDateTime.now());
            user4.setUpdatedAt(LocalDateTime.now());
            
            List<User> users = userRepository
                    .saveAll(Arrays.asList(user1, user2, user3, user4))
                    .collectList()
                    .block();
            
            logger.info("Loaded {} users", users != null ? users.size() : 0);
            
            // ========== SEED ORDERS ==========
            if (users != null && !users.isEmpty() && restaurants != null && !restaurants.isEmpty()) {
                String johnDoeId = users.get(0).getId();
                String janeSmithId = users.get(1).getId();
                String pizzaPalaceId = restaurants.get(0).getId();
                String sushiExpressId = restaurants.get(1).getId();
                
                Order order1 = new Order();
                order1.setUserId(johnDoeId);
                order1.setRestaurantId(pizzaPalaceId);
                
                Order.OrderItem item1 = new Order.OrderItem();
                item1.setFoodId("food1");
                item1.setFoodName("Margherita Pizza");
                item1.setQuantity(2);
                item1.setPrice(12.99);
                
                Order.OrderItem item2 = new Order.OrderItem();
                item2.setFoodId("food10");
                item2.setFoodName("Caesar Salad");
                item2.setQuantity(1);
                item2.setPrice(7.99);
                
                order1.setItems(Arrays.asList(item1, item2));
                order1.setTaxAmount(4.42); // 13% tax
                order1.setDeliveryFee(5.00);
                order1.setTotalPrice(43.39); // (12.99 * 2) + 7.99 + 4.42 + 5.00
                order1.setStatus("DELIVERED");
                order1.setPaymentMethod("CREDIT_CARD");
                order1.setPaymentStatus("COMPLETED");
                order1.setDeliveryAddress("100 Main Street, Toronto, ON M5H 2Y2");
                order1.setDeliveryCity("Toronto");
                order1.setDeliveryPostalCode("M5H 2Y2");
                order1.setSpecialInstructions("Ring doorbell");
                order1.setOrderDate(LocalDateTime.now().minusDays(2));
                order1.setActualDeliveryTime(LocalDateTime.now().minusDays(2).plusHours(1));
                
                Order order2 = new Order();
                order2.setUserId(janeSmithId);
                order2.setRestaurantId(sushiExpressId);
                
                Order.OrderItem item3 = new Order.OrderItem();
                item3.setFoodId("food3");
                item3.setFoodName("California Roll");
                item3.setQuantity(3);
                item3.setPrice(8.99);
                
                Order.OrderItem item4 = new Order.OrderItem();
                item4.setFoodId("food4");
                item4.setFoodName("Salmon Nigiri");
                item4.setQuantity(2);
                item4.setPrice(6.99);
                
                order2.setItems(Arrays.asList(item3, item4));
                order2.setTaxAmount(5.32); // 13% tax
                order2.setDeliveryFee(5.00);
                order2.setTotalPrice(51.27); // (8.99 * 3) + (6.99 * 2) + 5.32 + 5.00
                order2.setStatus("PREPARING");
                order2.setPaymentMethod("DEBIT_CARD");
                order2.setPaymentStatus("COMPLETED");
                order2.setDeliveryAddress("200 Oak Avenue, Mississauga, ON L5B 3C1");
                order2.setDeliveryCity("Mississauga");
                order2.setDeliveryPostalCode("L5B 3C1");
                order2.setSpecialInstructions("Leave at door");
                order2.setOrderDate(LocalDateTime.now().minusHours(1));
                
                List<Order> orders = orderRepository
                        .saveAll(Arrays.asList(order1, order2))
                        .collectList()
                        .block();
                
                logger.info("Loaded {} orders", orders != null ? orders.size() : 0);
            }
            
            logger.info("Seed data initialization completed successfully!");
        };
    }
}
