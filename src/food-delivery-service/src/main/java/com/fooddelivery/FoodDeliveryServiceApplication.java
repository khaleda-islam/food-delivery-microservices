package com.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FoodDeliveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodDeliveryServiceApplication.class, args);
		System.out.println("==========================================");
        System.out.println("Food Delivery Service Started Successfully!");
        System.out.println("==========================================");
	}

}
