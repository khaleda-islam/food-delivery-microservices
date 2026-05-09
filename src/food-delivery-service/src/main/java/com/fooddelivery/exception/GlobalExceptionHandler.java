/*
 * Food Delivery Service - Global Exception Handler
 * 
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 15, 2026
 * 
 * Purpose: Centralized exception handling for Food Delivery Service.
 * Handles database errors, validation errors, and other exceptions.
 */

package com.fooddelivery.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for Food Delivery Service.
 * 
 * Catches and handles exceptions from:
 * - Database operations (MongoDB)
 * - Validation errors
 * - Business logic exceptions
 * - General runtime exceptions
 */
@RestControllerAdvice(basePackages = "com.fooddelivery.controller")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors
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
     * Handle MongoDB and database connection errors
     * 
     * @param ex the Exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler({
            com.mongodb.MongoException.class,
            org.springframework.dao.DataAccessException.class
    })
    public Mono<ResponseEntity<Map<String, Object>>> handleDatabaseException(Exception ex) {
        
        logger.error("Database error: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        errorResponse.put("error", "Database Error");
        errorResponse.put("message", "Database operation failed. Please try again later.");
        errorResponse.put("details", ex.getMessage());
        errorResponse.put("exceptionType", ex.getClass().getSimpleName());
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse));
    }

    /**
     * Handle all other exceptions
     * 
     * @param ex the Exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        
        logger.error("Unexpected error: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred. Please try again later.");
        errorResponse.put("details", ex.getMessage());
        errorResponse.put("exceptionType", ex.getClass().getSimpleName());
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse));
    }
}
