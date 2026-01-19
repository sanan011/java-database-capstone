package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  Service service,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    /**
     * Save a new prescription
     * Endpoint: POST /{token}
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @RequestBody Prescription prescription,
            @PathVariable String token
    ) {
        // Validate doctor token
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }

        // Update appointment status to indicate prescription is added
        appointmentService.changeStatus(prescription.getAppointmentId());

        // Save prescription
        return prescriptionService.savePrescription(prescription);
    }

    /**
     * Get prescription by appointment ID
     * Endpoint: GET /{appointmentId}/{token}
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token
    ) {
        // Validate doctor token
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (tokenValidation.getStatusCode().isError()) {
            return ResponseEntity.status(tokenValidation.getStatusCode())
                    .body(Map.of("message", "Invalid or expired token"));
        }

        // Fetch prescription
        return prescriptionService.getPrescription(appointmentId);
    }
}