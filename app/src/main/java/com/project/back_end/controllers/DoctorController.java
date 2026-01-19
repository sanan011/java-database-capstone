package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.SharedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final SharedService service;

    public DoctorController(DoctorService doctorService, SharedService service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    /** 
     * Get doctor availability for a specific date
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, user);
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        List<String> availability = doctorService.getDoctorAvailability(doctorId, appointmentDate);
        return ResponseEntity.ok(Map.of("availability", availability));
    }

    /**
     * Get list of all doctors
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }

    /**
     * Add a new doctor (Admin only)
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired admin token"));
        }

        int result = doctorService.saveDoctor(doctor);
        if (result == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Doctor added to db"));
        } else if (result == -1) {
            return ResponseEntity.status(409).body(Map.of("message", "Doctor already exists"));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
        }
    }

    /**
     * Doctor login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    /**
     * Update doctor details (Admin only)
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired admin token"));
        }

        int result = doctorService.updateDoctor(doctor);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor updated"));
        } else if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
        }
    }

    /**
     * Delete doctor by ID (Admin only)
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired admin token"));
        }

        int result = doctorService.deleteDoctor(id);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
        } else if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found with id"));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
        }
    }

    /**
     * Filter doctors by name, time, and specialty
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ) {
        Map<String, Object> filteredDoctors = service.filterDoctor(name, speciality, time);
        return ResponseEntity.ok(filteredDoctors);
    }
}
