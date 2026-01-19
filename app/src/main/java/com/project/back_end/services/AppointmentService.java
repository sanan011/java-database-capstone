package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TokenService tokenService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
    }

    /**
     * Book a new appointment.
     * @param appointment the appointment to book
     * @return 1 if successful, 0 if an error occurs
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Update an existing appointment.
     * @param appointment the updated appointment data
     * @return ResponseEntity with success or error message
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
        Map<String, String> response = new HashMap<>();
        if (existing.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment oldAppointment = existing.get();

        // Validate patient
        if (!oldAppointment.getPatientId().equals(appointment.getPatientId())) {
            response.put("message", "Unauthorized: Patient ID mismatch");
            return ResponseEntity.status(403).body(response);
        }

        // Check doctor availability
        List<Appointment> overlapping = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                appointment.getDoctorId(),
                appointment.getAppointmentTime(),
                appointment.getAppointmentTime().plusHours(1)
        );
        if (!overlapping.isEmpty() && !overlapping.get(0).getId().equals(appointment.getId())) {
            response.put("message", "Doctor is not available at this time");
            return ResponseEntity.badRequest().body(response);
        }

        oldAppointment.setAppointmentTime(appointment.getAppointmentTime());
        oldAppointment.setStatus(appointment.getStatus());

        appointmentRepository.save(oldAppointment);
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an appointment.
     * @param appointmentId the appointment ID
     * @param patientId the patient requesting cancellation
     * @return ResponseEntity with success or error message
     */
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(Long appointmentId, Long patientId) {
        Optional<Appointment> existing = appointmentRepository.findById(appointmentId);
        Map<String, String> response = new HashMap<>();

        if (existing.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = existing.get();
        if (!appointment.getPatientId().equals(patientId)) {
            response.put("message", "Unauthorized: Patient ID mismatch");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment canceled successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get appointments for a doctor on a specific date, optionally filtered by patient name.
     * @param doctorId the doctor ID
     * @param date the date to filter appointments
     * @param patientName optional patient name filter
     * @return Map containing the list of appointments
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAppointments(Long doctorId, LocalDate date, String patientName) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments;
        if (patientName == null || patientName.isBlank()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, patientName, start, end
            );
        }

        Map<String, Object> result = new HashMap<>();
        result.put("appointments", appointments);
        return result;
    }

    /**
     * Change the status of an appointment.
     * @param appointmentId the appointment ID
     * @param status the new status
     */
    @Transactional
    public void changeStatus(Long appointmentId, int status) {
        appointmentRepository.updateStatus(status, appointmentId);
    }
}
