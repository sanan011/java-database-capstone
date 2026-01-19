package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return Collections.emptyList();

        Doctor doctor = doctorOpt.get();
        
        // FIXED: Explicitly parse String elements to LocalTime objects
        List<LocalTime> availableTimes = doctor.getAvailableTimes().stream()
                .map(t -> LocalTime.parse(t.toString()))
                .collect(Collectors.toCollection(ArrayList::new));

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        );

        // Remove booked slots (Comparing LocalTime to LocalTime)
        appointments.forEach(a -> availableTimes.remove(a.getAppointmentTime().toLocalTime()));

        return availableTimes.stream()
                .map(LocalTime::toString)
                .collect(Collectors.toList());
    }

    @Transactional
    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()) != null) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor) {
        if (doctorRepository.findById(doctor.getId()).isEmpty()) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(Long doctorId) {
        if (doctorRepository.findById(doctorId).isEmpty()) return -1;
        try {
            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());

        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("error", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        String token = tokenService.generateToken(doctor.getId(), "DOCTOR");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        response.put("doctors", doctors);
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecialtyAndTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByTimeAndSpecialty(String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findAll();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    // FIXED: Helper method ensures Strings are parsed to LocalTime for getHour()
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream()
                .filter(d -> d.getAvailableTimes() != null && d.getAvailableTimes().stream().anyMatch(t -> {
                    LocalTime time = LocalTime.parse(t.toString());
                    int hour = time.getHour();
                    return ("AM".equalsIgnoreCase(amOrPm) && hour < 12) ||
                           ("PM".equalsIgnoreCase(amOrPm) && hour >= 12);
                }))
                .collect(Collectors.toList());
    }
}