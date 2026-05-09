/*
 * Online Food Delivery System
 * Student Name: Khaleda Islam
 * Student ID: 301504989
 * Date: April 12, 2026
 * Description: Admin home controller for Thymeleaf admin panel
 */
package com.fooddelivery.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * Controller for admin panel home/dashboard
 */
@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    /**
     * Admin dashboard home page
     * GET /admin
     * @return admin home template
     */
    @GetMapping
    public Mono<String> home() {
        return Mono.just("admin/index");
    }
}
