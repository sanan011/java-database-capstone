package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.models.Login;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * Validates a JWT token for a user.
     */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        boolean valid = tokenService.isValidToken(token);
        if (!valid) {
            response.put("message", "Unauthorized: Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        response.put("message", "Token valid");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Validates admin credentials and generates a token if valid.
     */
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin == null) {
                response.put("message", "Unauthorized: Admin not found");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Unauthorized: Incorrect password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal Server Error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Filters doctors by name, specialty, and available time.
     */
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
    }

    /**
     * Validates if an appointment is available for booking.
     */
    public int validateAppointment(Appointment appointment) {
        if (!doctorRepository.existsById(appointment.getDoctorId())) return -1;

        List<String> availableSlots = doctorService.getDoctorAvailability(
                appointment.getDoctorId(),
                appointment.getAppointmentTime().toLocalDate()
        );

        return availableSlots.contains(appointment.getAppointmentTime().toLocalTime().toString()) ? 1 : 0;
    }

    /**
     * Validates if a patient can be registered (unique email or phone).
     */
    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    /**
     * Validates patient login and generates a token if successful.
     */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient == null || !patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Unauthorized: Invalid credentials");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal Server Error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Filters patient appointments based on condition (past/future) and/or doctor name.
     */
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            return patientService.filterByDoctorAndCondition(condition, name, token);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal Server Error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
