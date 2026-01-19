# User Stories

## Admin User Stories

### Admin Login
**Title:**  
_As an admin, I want to log into the portal using my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. Admin can access the login page.
2. Valid credentials allow successful login.
3. Invalid credentials display an error message.

**Priority:** High  
**Story Points:** 3  
**Notes:**
- Passwords must be securely stored and validated.

---

### Admin Logout
**Title:**  
_As an admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**
1. Logout option is available on all admin pages.
2. User session is terminated after logout.
3. Admin is redirected to the login page.

**Priority:** High  
**Story Points:** 2  
**Notes:**
- Prevent access to admin pages after logout.

---

### Add Doctor
**Title:**  
_As an admin, I want to add doctors to the portal, so that they can provide services to patients._

**Acceptance Criteria:**
1. Admin can create a new doctor profile.
2. Required fields are validated.
3. Doctor appears in the doctors list after creation.

**Priority:** High  
**Story Points:** 5  
**Notes:**
- Doctor credentials may be auto-generated.

---

### Delete Doctor Profile
**Title:**  
_As an admin, I want to delete a doctor’s profile from the portal, so that inactive profiles are removed._

**Acceptance Criteria:**
1. Admin can select a doctor to delete.
2. Deletion requires confirmation.
3. Doctor profile is removed from the system.

**Priority:** Medium  
**Story Points:** 3  
**Notes:**
- Existing appointments must be handled before deletion.

---

### View Monthly Appointment Statistics
**Title:**  
_As an admin, I want to run a stored procedure in MySQL CLI to retrieve monthly appointment counts, so that I can track system usage._

**Acceptance Criteria:**
1. Stored procedure executes successfully.
2. Monthly appointment totals are returned correctly.
3. Results can be used for reporting.

**Priority:** Medium  
**Story Points:** 5  
**Notes:**
- Stored procedure details should be documented.

---

## Patient User Stories

### View Doctors Without Login
**Title:**  
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**
1. Doctors list is accessible without authentication.
2. Doctor name and specialization are visible.
3. Booking is disabled for unauthenticated users.

**Priority:** Medium  
**Story Points:** 3  
**Notes:**
- Sensitive information should not be shown publicly.

---

### Patient Registration
**Title:**  
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. Patient can register with a valid email and password.
2. Duplicate email registrations are prevented.
3. Successful registration redirects the user.

**Priority:** High  
**Story Points:** 5  
**Notes:**
- Email validation is required.

---

### Patient Login
**Title:**  
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. Patient can log in with valid credentials.
2. Invalid credentials show an error message.
3. Successful login redirects to dashboard.

**Priority:** High  
**Story Points:** 3  
**Notes:**
- Consider account lockout on repeated failures.

---

### Patient Logout
**Title:**  
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**
1. Logout option is available.
2. Session is terminated after logout.
3. User is redirected to the home page.

**Priority:** High  
**Story Points:** 2  
**Notes:**
- Clear session tokens on logout.

---

### Book Appointment
**Title:**  
_As a patient, I want to book a one-hour appointment with a doctor, so that I can receive medical consultation._

**Acceptance Criteria:**
1. Patient can select a doctor and time slot.
2. Appointment duration is one hour.
3. Booking confirmation is displayed.

**Priority:** High  
**Story Points:** 8  
**Notes:**
- Prevent double booking.

---

### View Upcoming Appointments
**Title:**  
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. Upcoming appointments are listed.
2. Appointment date, time, and doctor are visible.
3. Only future appointments are shown.

**Priority:** Medium  
**Story Points:** 3  
**Notes:**
- Cancellation can be added later.

---

## Doctor User Stories

### Doctor Login
**Title:**  
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. Doctor can log in with valid credentials.
2. Invalid login attempts show errors.
3. Successful login opens dashboard.

**Priority:** High  
**Story Points:** 3  
**Notes:**
- Role-based access control required.

---

### Doctor Logout
**Title:**  
_As a doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**
1. Logout option is available.
2. Session is terminated.
3. Redirected to login page.

**Priority:** High  
**Story Points:** 2  
**Notes:**
- Clear cached data.

---

### View Appointment Calendar
**Title:**  
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**
1. Calendar displays upcoming appointments.
2. Appointment dates and times are accurate.
3. Only the doctor’s appointments are visible.

**Priority:** High  
**Story Points:** 5  
**Notes:**
- Weekly and daily views preferred.

---

### Mark Unavailability
**Title:**  
_As a doctor, I want to mark my unavailable times, so that patients can only book available slots._

**Acceptance Criteria:**
1. Doctor can block specific dates and times.
2. Blocked slots are not bookable.
3. Changes are saved successfully.

**Priority:** High  
**Story Points:** 5  
**Notes:**
- Support recurring unavailability if needed.

---

### Update Doctor Profile
**Title:**  
_As a doctor, I want to update my profile information, so that patients see accurate details._

**Acceptance Criteria:**
1. Doctor can edit specialization and contact info.
2. Inputs are validated.
3. Updated info is visible to patients.

**Priority:** Medium  
**Story Points:** 3  
**Notes:**
- Admin approval may be required.

---

### View Patient Details
**Title:**  
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**
1. Doctor can view patient name and appointment info.
2. Only assigned patients are visible.
3. Data is displayed securely.

**Priority:** High  
**Story Points:** 5  
**Notes:**
- Ensure patient data privacy.
