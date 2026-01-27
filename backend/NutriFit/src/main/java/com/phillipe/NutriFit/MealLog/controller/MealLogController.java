package com.phillipe.NutriFit.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MealLogController {


    @GetMapping("/food")
    public String meal(HttpServletRequest req) {
        return "Yummy meals: ";
    }

}
