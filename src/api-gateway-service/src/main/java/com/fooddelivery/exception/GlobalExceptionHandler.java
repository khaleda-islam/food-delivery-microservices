/*
 * API Gateway Service - Global Exception Handler
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 4, 2026
 * 
 * Purpose: Centralized exception handling for API Gateway Service.
 * Handles WebClient errors, service unavailability, and validation errors.
 * Returns user-friendly error messages to React frontend.
 */

package com.fooddelivery.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for API Gateway Service.
 * 
 * This handler catches exceptions from:
 * - WebClient communication errors (service unavailable, timeouts)
 * - Validation errors (invalid request data)
 * - HTTP errors from Food Delivery Service (404, 500, etc.)
 * - General runtime exceptions
 * 
 * All errors are logged and returned in a consistent JSON format
 * for easy handling in the React frontend.
 * 
 * Note: This handler applies only to controllers in com.fooddelivery.controller package
 * to avoid interfering with Swagger UI endpoints.
 */
@RestControllerAdvice(basePackages = "com.fooddelivery.controller")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle WebClient response exceptions
     * 
     * These occur when the Food Delivery Service returns an HTTP error status.
     * Examples: 404 Not Found, 500 Internal Server Error
     * 
     * @param ex the WebClientResponseException
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleWebClientResponseException(
            WebClientResponseException ex) {
        
        logger.error("WebClient error: {} - {}", ex.getStatusCode(), ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", ex.getStatusCode().value());
        errorResponse.put("error", ex.getStatusText());
        errorResponse.put("message", extractErrorMessage(ex));
        errorResponse.put("service", "Food Delivery Service");
        
        return Mono.just(ResponseEntity
                .status(ex.getStatusCode())
                .body(errorResponse));
    }

    /**
     * Handle validation errors
     * 
     * These occur when request body validation fails (@Valid annotation).
     * Example: Missing required fields, invalid email format
     * 
     * @param ex the WebExchangeBindException
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(
            WebExchangeBindException ex) {
        
        logger.error("Validation error: {}", ex.getMessage());
        
        // Extract field-specific validation errors
        Map<String, String> fieldErrors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
                ));
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Failed");
        errorResponse.put("message", "Request validation failed. Please check your input.");
        errorResponse.put("fieldErrors", fieldErrors);
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse));
    }

    /**
     * Handle invalid request input
     * 
     * These occur when request parameters are malformed.
     * Example: Invalid path variable type
     * 
     * @param ex the ServerWebInputException
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleServerWebInputException(
            ServerWebInputException ex) {
        
        logger.error("Invalid input: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", "Invalid request parameter or input");
        errorResponse.put("details", ex.getReason());
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse));
    }

    /**
     * Handle service unavailability
     * 
     * These occur when Food Delivery Service cannot be reached.
     * Examples: Service is down, network timeout, Eureka lookup failed
     * 
     * @param ex the Exception
     * @return ResponseEntity with service unavailable error
     */
    @ExceptionHandler({
            io.netty.channel.ConnectTimeoutException.class,
            java.net.ConnectException.class,
            java.util.concurrent.TimeoutException.class
    })
    public Mono<ResponseEntity<Map<String, Object>>> handleServiceUnavailableException(
            Exception ex) {
        
        logger.error("Service unavailable: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        errorResponse.put("error", "Service Unavailable");
        errorResponse.put("message", "Food Delivery Service is currently unavailable. Please try again later.");
        errorResponse.put("details", "The backend service could not be reached.");
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse));
    }

    /**
     * Handle all other exceptions
     * 
     * Catch-all handler for unexpected errors.
     * Prevents exposing internal error details to frontend.
     * 
     * @param ex the Exception
     * @return ResponseEntity with generic error message
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        
        logger.error("Unexpected error: {} - Type: {}", ex.getMessage(), ex.getClass().getName(), ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred. Please try again later.");
        
        // Expose internal error details for debugging (remove in production)
        errorResponse.put("details", ex.getMessage());
        errorResponse.put("exceptionType", ex.getClass().getSimpleName());
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse));
    }

    /**
     * Extract readable error message from WebClientResponseException
     * 
     * @param ex the exception
     * @return user-friendly error message
     */
    private String extractErrorMessage(WebClientResponseException ex) {
        try {
            // Try to extract message from response body
            String responseBody = ex.getResponseBodyAsString();
            if (responseBody != null && !responseBody.isEmpty()) {
                return "Error from Food Delivery Service: " + ex.getStatusText();
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        
        // Default messages based on status code
        return switch (ex.getStatusCode().value()) {
            case 404 -> "The requested resource was not found.";
            case 400 -> "Invalid request. Please check your input.";
            case 401 -> "Authentication required.";
            case 403 -> "Access denied.";
            case 500 -> "The backend service encountered an error.";
            case 503 -> "The backend service is temporarily unavailable.";
            default -> "An error occurred while processing your request.";
        };
    }
}
