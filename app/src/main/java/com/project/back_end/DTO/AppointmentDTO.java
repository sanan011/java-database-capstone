package com.project.back_end.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentDTO {

    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String patientAddress;
    private LocalDateTime appointmentTime;
    private int status;

    // Derived fields
    private LocalDate appointmentDate;
    private LocalTime appointmentTimeOnly;
    private LocalDateTime endTime;

    // Constructor
    // Add this to AppointmentDTO.java
    public AppointmentDTO(com.project.back_end.models.Appointment app) {
        this.id = app.getId();
        this.doctorId = app.getDoctor().getId();
        this.doctorName = app.getDoctor().getName();
        this.patientId = app.getPatient().getId();
        this.patientName = app.getPatient().getName();
        this.patientEmail = app.getPatient().getEmail();
        this.patientPhone = app.getPatient().getPhone();
        this.patientAddress = app.getPatient().getAddress();
        this.appointmentTime = app.getAppointmentTime();
        this.status = app.getStatus();
        
        if (this.appointmentTime != null) {
            this.appointmentDate = this.appointmentTime.toLocalDate();
            this.appointmentTimeOnly = this.appointmentTime.toLocalTime();
            this.endTime = this.appointmentTime.plusHours(1);
        }
    }

    // Getters
    public Long getId() { return id; }
    public Long getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public Long getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getPatientEmail() { return patientEmail; }
    public String getPatientPhone() { return patientPhone; }
    public String getPatientAddress() { return patientAddress; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public int getStatus() { return status; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getAppointmentTimeOnly() { return appointmentTimeOnly; }
    public LocalDateTime getEndTime() { return endTime; }
}