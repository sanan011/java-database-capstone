package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Save a new prescription.
     * Prevents saving duplicate prescriptions for the same appointment.
     *
     * @param prescription The prescription object to save
     * @return ResponseEntity containing the status and message
     */
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            // Check if a prescription already exists for the appointment
            List<Prescription> existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (!existing.isEmpty()) {
                response.put("message", "Prescription already exists for this appointment");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Save prescription
            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal server error while saving prescription");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieve prescriptions for a specific appointment.
     *
     * @param appointmentId The appointment ID
     * @return ResponseEntity containing prescription details or error message
     */
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescriptions.isEmpty()) {
                response.put("message", "No prescription found for this appointment");
            } else {
                response.put("prescriptions", prescriptions);
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal server error while fetching prescription");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
