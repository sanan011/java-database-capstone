package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.SharedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final SharedService service;

    public PatientController(PatientService patientService, SharedService service) {
        this.patientService = patientService;
        this.service = service;
    }

    /**
     * Get patient details using JWT token
     */
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }
        return patientService.getPatientDetails(token);
    }

    /**
     * Register a new patient
     */
    @PostMapping()
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        boolean validPatient = service.validatePatient(patient);
        if (!validPatient) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Patient with email or phone number already exists"));
        }

        int result = patientService.createPatient(patient);
        if (result == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Signup successful"));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    /**
     * Patient login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    /**
     * Get all appointments for a patient
     */
    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }
        return patientService.getPatientAppointment(id, token);
    }

    /**
     * Filter patient appointments by condition or doctor name
     */
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }
        return service.filterPatient(condition, name, token);
    }
}