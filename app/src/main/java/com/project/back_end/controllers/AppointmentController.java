package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.SharedService;
import com.project.back_end.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SharedService service;
    private final TokenService tokenService;

    public AppointmentController(AppointmentService appointmentService, 
                                 SharedService service,
                                 TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.service = service;
        this.tokenService = tokenService;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }

        Long doctorId = tokenService.extractId(token); 
        LocalDate appointmentDate = LocalDate.parse(date);
        
        // Fixed: Correct parameter order - doctorId, date, patientName
        Map<String, Object> appointments = appointmentService.getAppointments(doctorId, appointmentDate, patientName);
        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token
    ) {
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

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }

        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable Long id, 
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }

        Long patientId = tokenService.extractId(token);
        return appointmentService.cancelAppointment(id, patientId);
    }
}