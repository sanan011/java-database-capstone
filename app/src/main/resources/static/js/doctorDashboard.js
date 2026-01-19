// app/src/main/resources/static/js/doctorDashboard.js

import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// DOM Elements
const tableBody = document.getElementById('patientTableBody');
const searchInput = document.getElementById('searchBar');
const todayButton = document.getElementById('todayButton');
const datePicker = document.getElementById('datePicker');

// Global variables
let selectedDate = new Date().toISOString().split('T')[0]; // YYYY-MM-DD
let token = localStorage.getItem('token');
let patientName = null;

// Search bar: filter by patient name
if (searchInput) {
  searchInput.addEventListener('input', () => {
    const value = searchInput.value.trim();
    patientName = value !== '' ? value : null;
    loadAppointments();
  });
}

// "Today" button: reset date to today
if (todayButton) {
  todayButton.addEventListener('click', () => {
    selectedDate = new Date().toISOString().split('T')[0];
    if (datePicker) datePicker.value = selectedDate;
    loadAppointments();
  });
}

// Date picker: load appointments for selected date
if (datePicker) {
  datePicker.addEventListener('change', () => {
    selectedDate = datePicker.value;
    loadAppointments();
  });
}

/**
 * Load and render appointments for the doctor
 */
async function loadAppointments() {
  try {
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    // Clear existing rows
    tableBody.innerHTML = '';

    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = `
        <tr>
          <td colspan="5" class="text-center">No Appointments found for the selected date.</td>
        </tr>
      `;
      return;
    }

    // Render each appointment as a table row
    appointments.forEach(app => {
      const patient = {
        id: app.patientId,
        name: app.patientName,
        phone: app.patientPhone,
        email: app.patientEmail
      };
      const row = createPatientRow(patient, app); // Pass appointment object for additional details
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error('Error loading appointments:', error);
    tableBody.innerHTML = `
      <tr>
        <td colspan="5" class="text-center">Error loading appointments. Please try again later.</td>
      </tr>
    `;
  }
}

// Initial render on page load
window.addEventListener('DOMContentLoaded', () => {
  if (typeof renderContent === 'function') renderContent(); // Optional UI setup
  loadAppointments(); // Load today's appointments by default
});
