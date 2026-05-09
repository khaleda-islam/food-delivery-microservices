/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 12, 2026
 * Description: Admin controller for Customer/User management with Thymeleaf views
 */
package com.fooddelivery.controller.admin;

import com.fooddelivery.model.User;
import com.fooddelivery.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Admin controller for managing customers/users via Thymeleaf interface
 */
@Controller
@RequestMapping("/admin/customers")
public class AdminCustomerController {

    private static final Logger logger = LoggerFactory.getLogger(AdminCustomerController.class);

    @Autowired
    private UserService service;

    /**
     * List all customers
     * GET /admin/customers
     */
    @GetMapping
    public String listCustomers(Model model) {
        logger.info("Admin: Listing all customers");
        model.addAttribute("customers", service.findAll());
        return "admin/customers/list";
    }

    /**
     * Show form for creating a new customer
     * GET /admin/customers/new
     */
    @GetMapping("/new")
    public String newCustomerForm(Model model) {
        logger.info("Admin: New customer form");
        model.addAttribute("customer", new User());
        model.addAttribute("isEdit", false);
        return "admin/customers/form";
    }

    /**
     * Create a new customer
     * POST /admin/customers
     */
    @PostMapping
    public Mono<String> createCustomer(@Valid @ModelAttribute("customer") User customer, 
                                       BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Admin: Validation errors creating customer");
            model.addAttribute("isEdit", false);
            return Mono.just("admin/customers/form");
        }
        
        logger.info("Admin: Creating customer: {}", customer.getName());
        customer.setId(null); // Ensure new customer
        return service.registerUser(customer)
                .doOnSuccess(saved -> logger.info("Admin: Customer created with ID: {}", saved.getId()))
                .thenReturn("redirect:/admin/customers");
    }

    /**
     * Show form for editing a customer
     * GET /admin/customers/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public Mono<String> editCustomerForm(@PathVariable String id, Model model) {
        logger.info("Admin: Edit customer form for ID: {}", id);
        return service.findById(id)
                .doOnNext(customer -> {
                    model.addAttribute("customer", customer);
                    model.addAttribute("isEdit", true);
                })
                .thenReturn("admin/customers/form")
                .switchIfEmpty(Mono.just("redirect:/admin/customers"));
    }

    /**
     * Update a customer
     * POST /admin/customers/update/{id}
     */
    @PostMapping("/update/{id}")
    public Mono<String> updateCustomer(@PathVariable String id, 
                                       @Valid @ModelAttribute("customer") User customer,
                                       BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Admin: Validation errors updating customer");
            model.addAttribute("isEdit", true);
            return Mono.just("admin/customers/form");
        }
        
        logger.info("Admin: Updating customer ID: {}", id);
        customer.setId(id);
        return service.updateUser(customer)
                .doOnSuccess(saved -> logger.info("Admin: Customer updated: {}", saved.getId()))
                .thenReturn("redirect:/admin/customers");
    }

    /**
     * Delete a customer
     * GET /admin/customers/delete/{id}
     */
    @GetMapping("/delete/{id}")
    public Mono<String> deleteCustomer(@PathVariable String id) {
        logger.info("Admin: Deleting customer ID: {}", id);
        return service.deleteById(id)
                .doOnSuccess(v -> logger.info("Admin: Customer deleted: {}", id))
                .thenReturn("redirect:/admin/customers");
    }

    /**
     * View customer profile details
     * GET /admin/customers/{id}
     */
    @GetMapping("/{id}")
    public Mono<String> viewCustomerProfile(@PathVariable String id, Model model) {
        logger.info("Admin: Viewing customer profile ID: {}", id);
        return service.findById(id)
                .doOnNext(customer -> model.addAttribute("customer", customer))
                .thenReturn("admin/customers/profile")
                .switchIfEmpty(Mono.just("redirect:/admin/customers"));
    }
}
