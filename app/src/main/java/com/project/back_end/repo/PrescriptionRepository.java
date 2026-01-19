package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Find all prescriptions associated with a specific appointment.
     *
     * @param appointmentId the ID of the appointment
     * @return a list of Prescription entities
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
}
