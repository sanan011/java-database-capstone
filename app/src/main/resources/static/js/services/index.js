// app/src/main/resources/static/js/services/index.js

// Import required modules
import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

// Define API endpoints
const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

// Ensure DOM elements exist before attaching listeners
window.onload = function () {
  const adminBtn = document.getElementById('adminLogin');
  const doctorBtn = document.getElementById('doctorLogin');

  if (adminBtn) {
    adminBtn.addEventListener('click', () => openModal('adminLogin'));
  }

  if (doctorBtn) {
    doctorBtn.addEventListener('click', () => openModal('doctorLogin'));
  }
};

// Admin login handler
window.adminLoginHandler = async () => {
  try {
    const username = document.getElementById('adminUsername').value;
    const password = document.getElementById('adminPassword').value;

    const admin = { username, password };

    const response = await fetch(ADMIN_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(admin),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      selectRole('admin'); // Set role and handle admin-specific rendering
    } else {
      alert('Invalid credentials!');
    }
  } catch (error) {
    console.error('Admin login error:', error);
    alert('Something went wrong. Please try again later.');
  }
};

// Doctor login handler
window.doctorLoginHandler = async () => {
  try {
    const email = document.getElementById('doctorEmail').value;
    const password = document.getElementById('doctorPassword').value;

    const doctor = { email, password };

    const response = await fetch(DOCTOR_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      selectRole('doctor'); // Set role and handle doctor-specific rendering
    } else {
      alert('Invalid credentials!');
    }
  } catch (error) {
    console.error('Doctor login error:', error);
    alert('Something went wrong. Please try again later.');
  }
};