package com.project.back_end.mvc;

// FIXED: Changed .service to .services to match your folder structure
import com.project.back_end.services.SharedService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    @Autowired
    private SharedService sharedService;

    // Admin Dashboard Endpoint
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {
        // FIXED: sharedService.validateToken returns a ResponseEntity. 
        // We check if the status code is 200 (OK).
        boolean isValid = sharedService.validateToken(token, "admin").getStatusCode() == HttpStatus.OK;

        if (isValid) {
            return "admin/adminDashboard";
        } else {
            return "redirect:/";
        }
    }

    // Doctor Dashboard Endpoint
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {
        // FIXED: Same logic here for the doctor role
        boolean isValid = sharedService.validateToken(token, "doctor").getStatusCode() == HttpStatus.OK;

        if (isValid) {
            return "doctor/doctorDashboard";
        } else {
            return "redirect:/";
        }
    }
}