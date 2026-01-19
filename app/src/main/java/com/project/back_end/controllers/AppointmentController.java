package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments") // Base path for appointment endpoints
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    // Constructor injection for AppointmentService and Service
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    /**
     * Get appointments by date and patient name
     * Only accessible by doctors
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token
    ) {
        // Validate doctor token
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        Map<String, Object> appointments = appointmentService.getAppointment(patientName, appointmentDate, token);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Book a new appointment
     * Only accessible by patients
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token
    ) {
        // Validate patient token
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }

        int validationResult = service.validateAppointment(appointment);
        if (validationResult == -1) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid doctor ID"));
        } else if (validationResult == 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Selected time slot is unavailable"));
        }

        int bookingResult = appointmentService.bookAppointment(appointment);
        if (bookingResult == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Appointment booked successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to book appointment"));
        }
    }

    /**
     * Update an existing appointment
     * Only accessible by patients
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token
    ) {
        // Validate patient token
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }

        return appointmentService.updateAppointment(appointment);
    }

    /**
     * Cancel an appointment
     * Only accessible by patients
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token
    ) {
        // Validate patient token
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }

        return appointmentService.cancelAppointment(id, token);
    }
}
