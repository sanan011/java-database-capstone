// app/src/main/resources/static/js/patientDashboard.js

import { createDoctorCard } from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { patientSignup, patientLogin } from './services/patientServices.js';

// Load doctors on page load
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
  bindModalButtons();
  bindFilterEvents();
});

// Modal Button Bindings
function bindModalButtons() {
  const signupBtn = document.getElementById("patientSignup");
  const loginBtn = document.getElementById("patientLogin");

  if (signupBtn) signupBtn.addEventListener("click", () => openModal("patientSignup"));
  if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));
}

// Load all doctors
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Failed to load doctors:", error);
    document.getElementById("content").innerHTML = "<p>❌ Unable to load doctors.</p>";
  }
}

// Render doctor cards
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors available.</p>";
    return;
  }

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// Filter functionality
function bindFilterEvents() {
  const searchInput = document.getElementById("searchBar");
  const timeFilter = document.getElementById("filterTime");
  const specialtyFilter = document.getElementById("filterSpecialty");

  if (searchInput) searchInput.addEventListener("input", filterDoctorsOnChange);
  if (timeFilter) timeFilter.addEventListener("change", filterDoctorsOnChange);
  if (specialtyFilter) specialtyFilter.addEventListener("change", filterDoctorsOnChange);
}

async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar").value.trim() || null;
    const time = document.getElementById("filterTime").value || null;
    const specialty = document.getElementById("filterSpecialty").value || null;

    const response = await filterDoctors(name, time, specialty);
    const doctors = response.doctors || [];

    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      document.getElementById("content").innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (error) {
    console.error("Failed to filter doctors:", error);
    alert("❌ An error occurred while filtering doctors.");
  }
}

// Patient Signup
window.signupPatient = async function () {
  try {
    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const address = document.getElementById("address").value.trim();

    const { success, message } = await patientSignup({ name, email, password, phone, address });

    if (success) {
      alert(`✅ ${message}`);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    } else {
      alert(`❌ ${message}`);
    }
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};

// Patient Login
window.loginPatient = async function () {
  try {
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    const response = await patientLogin({ email, password });

    if (response.ok) {
      const result = await response.json();
      localStorage.setItem('token', result.token);
      selectRole('loggedPatient'); // role used for rendering patient dashboard
      window.location.href = '/pages/loggedPatientDashboard.html';
    } else {
      alert('❌ Invalid credentials!');
    }
  } catch (error) {
    console.error("Login failed:", error);
    alert("❌ Failed to login. Please try again.");
  }
};
