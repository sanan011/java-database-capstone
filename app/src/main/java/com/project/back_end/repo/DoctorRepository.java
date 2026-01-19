package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Find a doctor by email
    Doctor findByEmail(String email);

    // Find doctors by partial name match (case-sensitive)
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(String name);

    // Filter doctors by partial name (case-insensitive) and specialty (case-insensitive)
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

    // Find doctors by specialty (case-insensitive)
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
