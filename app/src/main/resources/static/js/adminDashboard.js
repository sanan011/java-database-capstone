// app/src/main/resources/static/js/adminDashboard.js

// Import required modules
import { openModal } from '../components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from '../services/doctorServices.js';
import { createDoctorCard } from '../components/doctorCard.js';

// DOM Elements
const addDocBtn = document.getElementById('addDocBtn');
const contentDiv = document.getElementById('content');
const searchBar = document.getElementById('searchBar');
const filterTime = document.getElementById('filterTime');
const filterSpecialty = document.getElementById('filterSpecialty');

// Open Add Doctor Modal
if (addDocBtn) {
  addDocBtn.addEventListener('click', () => openModal('addDoctor'));
}

// Load doctor cards on page load
window.addEventListener('DOMContentLoaded', loadDoctorCards);

// Event listeners for search/filter
if (searchBar) searchBar.addEventListener('input', filterDoctorsOnChange);
if (filterTime) filterTime.addEventListener('change', filterDoctorsOnChange);
if (filterSpecialty) filterSpecialty.addEventListener('change', filterDoctorsOnChange);

/**
 * Fetch all doctors and render them
 */
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error('Error loading doctors:', error);
  }
}

/**
 * Filter doctors based on search bar and filters
 */
async function filterDoctorsOnChange() {
  try {
    const name = searchBar.value.trim() || null;
    const time = filterTime.value || null;
    const specialty = filterSpecialty.value || null;

    const { doctors } = await filterDoctors(name, time, specialty);

    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      contentDiv.innerHTML = `<p>No doctors found with the given filters.</p>`;
    }
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('Failed to filter doctors. Please try again.');
  }
}

/**
 * Render a list of doctors using createDoctorCard
 * @param {Array} doctors 
 */
function renderDoctorCards(doctors) {
  contentDiv.innerHTML = ''; // Clear existing content
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/**
 * Collect data from the Add Doctor modal and save a new doctor
 */
window.adminAddDoctor = async () => {
  try {
    const name = document.getElementById('doctorName').value.trim();
    const email = document.getElementById('doctorEmail').value.trim();
    const phone = document.getElementById('doctorPhone').value.trim();
    const password = document.getElementById('doctorPassword').value.trim();
    const specialty = document.getElementById('doctorSpecialty').value.trim();
    const availableTimes = Array.from(document.querySelectorAll('input[name="availableTimes"]:checked'))
      .map(input => input.value);

    const token = localStorage.getItem('token');
    if (!token) {
      alert('Authentication token not found. Please log in again.');
      return;
    }

    const doctor = { name, email, phone, password, specialty, availableTimes };

    const { success, message } = await saveDoctor(doctor, token);

    if (success) {
      alert('Doctor added successfully!');
      document.getElementById('addDoctorForm').reset();
      openModal('addDoctor', false); // Close modal
      loadDoctorCards(); // Refresh doctor list
    } else {
      alert(`Failed to add doctor: ${message}`);
    }
  } catch (error) {
    console.error('Error adding doctor:', error);
    alert('Something went wrong. Please try again.');
  }
};