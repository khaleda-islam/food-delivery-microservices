/*
 * API Gateway Service - WebClient Configuration
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * 
 * Purpose: Configures WebClient with @LoadBalanced for Eureka service discovery.
 * This enables calling other microservices using their Eureka registration names
 * instead of hardcoded URLs (e.g., http://food-delivery-service/api/restaurants).
 */

package com.fooddelivery.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient Configuration for API Gateway Service
 * 
 * This configuration class sets up WebClient beans for making HTTP requests
 * to other microservices in the system using Eureka service discovery.
 * 
 * Key Features:
 * - @LoadBalanced: Enables Eureka-based service discovery and client-side load balancing
 * - Reactive: Uses WebFlux WebClient for non-blocking communication
 * - Service Name Resolution: Can call services like "http://food-delivery-service/api/..."
 * 
 * Architecture Flow:
 * API Gateway → WebClient → Eureka → Food Delivery Service
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates a load-balanced WebClient.Builder bean.
     * 
     * The @LoadBalanced annotation enables:
     * 1. Service Discovery: Resolves service names through Eureka
     * 2. Client-Side Load Balancing: Distributes requests across multiple instances
     * 3. Fault Tolerance: Can integrate with Resilience4j for circuit breakers
     * 
     * Example Usage:
     * Instead of: http://localhost:8080/api/restaurants
     * Use: http://food-delivery-service/api/restaurants
     * 
     * @return WebClient.Builder configured for load balancing
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Creates the actual WebClient instance used by service classes.
     * 
     * This WebClient will be injected into client service classes
     * (e.g., RestaurantClientService, FoodClientService) to make
     * HTTP requests to the Food Delivery Service.
     * 
     * Benefits of WebClient over RestTemplate:
     * - Non-blocking and reactive (returns Mono/Flux)
     * - Better performance with reactive stack
     * - Supports streaming responses
     * - More modern and recommended by Spring
     * 
     * @param builder The load-balanced WebClient.Builder
     * @return WebClient instance ready for use
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
