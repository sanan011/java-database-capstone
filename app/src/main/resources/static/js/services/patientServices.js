// app/src/main/resources/static/js/services/patientServices.js

// Import API Base URL
import { API_BASE_URL } from "../config/config.js";

// Base endpoint for all patient-related API calls
const PATIENT_API = API_BASE_URL + '/patient';

/**
 * Patient Signup
 * @param {Object} data - { name, email, password, ... }
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function patientSignup(data) {
  try {
    // Send POST request to create a new patient
    const response = await fetch(PATIENT_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    const result = await response.json();

    // Throw error if server responds with failure
    if (!response.ok) throw new Error(result.message || "Signup failed");

    return { success: true, message: result.message };
  } catch (error) {
    console.error("Error :: patientSignup ::", error);
    return { success: false, message: error.message };
  }
}

/**
 * Patient Login
 * @param {Object} data - { email, password }
 * @returns {Promise<{success: boolean, token?: string, message: string}>}
 */
export async function patientLogin(data) {
  try {
    // Send POST request to login endpoint
    const response = await fetch(`${PATIENT_API}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    const result = await response.json();

    if (!response.ok) return { success: false, message: result.message || "Login failed" };

    return { success: true, token: result.token, message: result.message || "Login successful" };
  } catch (error) {
    console.error("Error :: patientLogin ::", error);
    return { success: false, message: "Network or server error" };
  }
}

/**
 * Get logged-in patient data
 * @param {string} token - Authentication token
 * @returns {Promise<Object|null>} - Patient object or null if failed
 */
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/${token}`);
    const data = await response.json();

    return response.ok ? data.patient : null;
  } catch (error) {
    console.error("Error fetching patient details:", error);
    return null;
  }
}

/**
 * Get patient appointments (works for patient or doctor dashboards)
 * @param {string|number} id - Patient ID
 * @param {string} token - Auth token
 * @param {string} user - "patient" or "doctor"
 * @returns {Promise<Array|null>} - Array of appointments or null
 */
export async function getPatientAppointments(id, token, user) {
  try {
    const response = await fetch(`${PATIENT_API}/${id}/${user}/${token}`);
    const data = await response.json();

    return response.ok ? data.appointments : null;
  } catch (error) {
    console.error("Error fetching patient appointments:", error);
    return null;
  }
}

/**
 * Filter appointments based on condition and name
 * @param {string} condition - e.g., "pending", "consulted"
 * @param {string} name - doctor/patient name
 * @param {string} token - Auth token
 * @returns {Promise<{appointments: Array}>} - Filtered appointments or empty array
 */
export async function filterAppointments(condition, name, token) {
  try {
    const response = await fetch(`${PATIENT_API}/filter/${condition}/${name}/${token}`, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      console.error("Failed to fetch appointments:", response.statusText);
      return { appointments: [] };
    }

    const data = await response.json();
    return { appointments: data.appointments || [] };
  } catch (error) {
    console.error("Error filtering appointments:", error);
    alert("Something went wrong while fetching appointments!");
    return { appointments: [] };
  }
}
