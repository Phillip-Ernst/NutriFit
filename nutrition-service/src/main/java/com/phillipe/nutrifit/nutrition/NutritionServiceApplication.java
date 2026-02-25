package com.phillipe.nutrifit.nutrition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class NutritionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NutritionServiceApplication.class, args);
    }
}