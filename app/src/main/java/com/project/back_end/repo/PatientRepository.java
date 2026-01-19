package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by email.
     * @param email the email address of the patient
     * @return the Patient entity if found, otherwise null
     */
    Patient findByEmail(String email);

    /**
     * Find a patient by email or phone number.
     * @param email the email address of the patient
     * @param phone the phone number of the patient
     * @return the Patient entity if found, otherwise null
     */
    Patient findByEmailOrPhone(String email, String phone);
}
