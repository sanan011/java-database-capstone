// app/src/main/resources/static/js/services/doctorServices.js

// Import API Base URL from config
import { API_BASE_URL } from "../config/config.js";

// Define the Doctor API base endpoint
const DOCTOR_API = API_BASE_URL + '/doctor';

/**
 * Fetch all doctors
 * @returns {Promise<Array>} - Array of doctor objects
 */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API, { method: 'GET' });
    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error('Error fetching doctors:', error);
    return [];
  }
}

/**
 * Delete a doctor by ID
 * @param {string|number} id - Doctor ID
 * @param {string} token - Admin authentication token
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
      method: 'DELETE',
    });
    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || 'Deletion failed'
    };
  } catch (error) {
    console.error('Error deleting doctor:', error);
    return { success: false, message: 'Failed to delete doctor' };
  }
}

/**
 * Save (add) a new doctor
 * @param {Object} doctor - Doctor details object
 * @param {string} token - Admin authentication token
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor),
    });
    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || 'Failed to save doctor'
    };
  } catch (error) {
    console.error('Error saving doctor:', error);
    return { success: false, message: 'Failed to save doctor' };
  }
}

/**
 * Filter doctors by name, availability time, and specialty
 * @param {string} name - Doctor name filter
 * @param {string} time - Available time filter
 * @param {string} specialty - Specialty filter
 * @returns {Promise<{doctors: Array}>}
 */
export async function filterDoctors(name = '', time = '', specialty = '') {
  try {
    const url = `${DOCTOR_API}/filter/${name}/${time}/${specialty}`;
    const response = await fetch(url, { method: 'GET' });

    if (response.ok) {
      const data = await response.json();
      return { doctors: data.doctors || [] };
    } else {
      console.error('Error filtering doctors:', response.statusText);
      return { doctors: [] };
    }
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('Failed to filter doctors. Please try again later.');
    return { doctors: [] };
  }
}