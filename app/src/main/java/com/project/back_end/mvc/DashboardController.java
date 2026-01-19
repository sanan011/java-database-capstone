package com.project.back_end.mvc;

import com.project.back_end.service.SharedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    // =========================
    // Autowire the shared service for token validation
    // =========================
    @Autowired
    private SharedService sharedService;

    // =========================
    // Admin Dashboard Endpoint
    // =========================
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {
        // Validate token for admin role
        boolean isValid = sharedService.validateToken(token, "admin");

        if (isValid) {
            // Token valid → return Thymeleaf admin dashboard view
            return "admin/adminDashboard";
        } else {
            // Token invalid → redirect to login/home page
            return "redirect:/";
        }
    }

    // =========================
    // Doctor Dashboard Endpoint
    // =========================
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {
        // Validate token for doctor role
        boolean isValid = sharedService.validateToken(token, "doctor");

        if (isValid) {
            // Token valid → return Thymeleaf doctor dashboard view
            return "doctor/doctorDashboard";
        } else {
            // Token invalid → redirect to login/home page
            return "redirect:/";
        }
    }
}