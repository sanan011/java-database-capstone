# Schema Design

This document describes the database schema design for the clinic management system.
It includes relational data modeled in MySQL and flexible document-based data modeled in MongoDB.

---

## MySQL Database Design

MySQL is used for structured, validated, and interrelated operational data such as users and appointments.

### Table: patients
- id: INT, Primary Key, AUTO_INCREMENT
- first_name: VARCHAR(100), NOT NULL
- last_name: VARCHAR(100), NOT NULL
- email: VARCHAR(255), NOT NULL, UNIQUE
- password_hash: VARCHAR(255), NOT NULL
- phone: VARCHAR(20), UNIQUE
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP

**Notes:**
- Email uniqueness prevents duplicate accounts.
- Passwords are stored as hashes.
- Patient appointment history should be retained even if the patient account is deleted.

---

### Table: doctors
- id: INT, Primary Key, AUTO_INCREMENT
- first_name: VARCHAR(100), NOT NULL
- last_name: VARCHAR(100), NOT NULL
- specialization: VARCHAR(150), NOT NULL
- email: VARCHAR(255), NOT NULL, UNIQUE
- phone: VARCHAR(20), UNIQUE
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
- is_active: BOOLEAN, NOT NULL, DEFAULT TRUE

**Notes:**
- Doctors are soft-deleted using `is_active` to preserve appointment history.
- A doctor should not be allowed overlapping appointments (handled via application logic).

---

### Table: admin
- id: INT, Primary Key, AUTO_INCREMENT
- username: VARCHAR(100), NOT NULL, UNIQUE
- password_hash: VARCHAR(255), NOT NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP

**Notes:**
- Admin accounts are limited and managed manually.
- Used for system management tasks.

---

### Table: appointments
- id: INT, Primary Key, AUTO_INCREMENT
- doctor_id: INT, NOT NULL, Foreign Key → doctors(id)
- patient_id: INT, NOT NULL, Foreign Key → patients(id)
- appointment_time: DATETIME, NOT NULL
- duration_minutes: INT, NOT NULL, DEFAULT 60
- status: INT, NOT NULL  
  - 0 = Scheduled  
  - 1 = Completed  
  - 2 = Cancelled
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP

**Notes:**
- Appointments should not overlap for the same doctor.
- If a patient is deleted, appointments should remain for historical records.
- Cancellation does not remove the appointment row.

---

### Table: doctor_availability
- id: INT, Primary Key, AUTO_INCREMENT
- doctor_id: INT, NOT NULL, Foreign Key → doctors(id)
- available_from: DATETIME, NOT NULL
- available_to: DATETIME, NOT NULL
- is_available: BOOLEAN, NOT NULL, DEFAULT TRUE

**Notes:**
- Doctors define availability windows.
- Patients can only book appointments within available time ranges.

---

### Table: clinic_locations (Optional)
- id: INT, Primary Key, AUTO_INCREMENT
- name: VARCHAR(150), NOT NULL
- address: VARCHAR(255), NOT NULL
- phone: VARCHAR(20)

**Notes:**
- Useful if the clinic expands to multiple locations.

---

## MongoDB Collection Design

MongoDB is used for flexible, semi-structured data that does not fit well into rigid relational tables.
This includes free-form doctor notes, optional patient feedback, prescriptions, logs, and messages.

### Collection: prescriptions

This collection stores prescription data linked to a specific appointment.
It allows flexible metadata, doctor notes, and future schema evolution.

```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours",
      "durationDays": 5
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "frequency": "Twice a day",
      "durationDays": 3
    }
  ],
  "doctorNotes": "Patient should take medication after meals.",
  "tags": ["pain", "fever"],
  "refillAllowed": true,
  "metadata": {
    "createdVia": "Doctor Portal",
    "version": 1
  },
  "createdAt": "2026-01-18T15:30:00Z"
}
