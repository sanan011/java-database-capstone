package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}admin") // Base path from application.properties
public class AdminController {

    private final Service service;

    // Constructor injection for the service
    public AdminController(Service service) {
        this.service = service;
    }

    /**
     * Admin login endpoint
     * URL: POST /api/admin/login
     * Body: JSON with "username" and "password"
     * Returns: JWT token if credentials are correct, error message otherwise
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }
}
