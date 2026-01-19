package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /** Create a new patient */
    @Transactional
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** Get appointments for a patient using ID and token */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null || !patient.getId().equals(id)) {
                response.put("message", "Unauthorized access");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            List<AppointmentDTO> appointments = appointmentRepository.findByPatientId(id).stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
            response.put("appointments", appointments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** Filter appointments by condition: past or future */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        int status;
        if ("future".equalsIgnoreCase(condition)) status = 0;
        else if ("past".equalsIgnoreCase(condition)) status = 1;
        else {
            response.put("message", "Invalid condition");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<AppointmentDTO> filteredAppointments = appointmentRepository
                .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status).stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", filteredAppointments);
        return ResponseEntity.ok(response);
    }

    /** Filter appointments by doctor's name */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(String doctorName, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AppointmentDTO> filteredAppointments = appointmentRepository
                    .filterByDoctorNameAndPatientId(doctorName, patientId).stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
            response.put("appointments", filteredAppointments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** Filter appointments by doctor's name and condition (past/future) */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String doctorName, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        int status;
        if ("future".equalsIgnoreCase(condition)) status = 0;
        else if ("past".equalsIgnoreCase(condition)) status = 1;
        else {
            response.put("message", "Invalid condition");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<AppointmentDTO> filteredAppointments = appointmentRepository
                .filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status).stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", filteredAppointments);
        return ResponseEntity.ok(response);
    }

    /** Get patient details based on JWT token */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                response.put("message", "Patient not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.put("patient", patient);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
