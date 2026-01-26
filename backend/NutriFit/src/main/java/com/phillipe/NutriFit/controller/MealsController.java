package com.phillipe.NutriFit.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class MealsController {


    @GetMapping("/food")
    public String meal(HttpServletRequest req) {
        return "Yummy meals: ";
    }

}
