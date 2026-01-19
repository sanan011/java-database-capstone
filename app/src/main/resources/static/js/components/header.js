// header.js

// Main function to render the header dynamically
function renderHeader() {
  const headerDiv = document.getElementById("header");

  // 1. Skip role-based header on the homepage
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  // 2. Get user role and token from localStorage
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // 3. Handle invalid/expired sessions
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  // 4. Start building header HTML
  let headerContent = `<header class="header">
    <div class="logo-section">
      <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
      <span class="logo-title">Hospital CMS</span>
    </div>
    <nav>`;

  // 5. Role-specific buttons
  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" id="logoutBtn">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
      <button id="homeBtn" class="adminBtn" onclick="selectRole('doctor')">Home</button>
      <a href="#" id="logoutBtn">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="homeBtn" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <a href="#" id="logoutPatientBtn">Logout</a>`;
  }

  // 6. Close nav and header
  headerContent += `</nav></header>`;

  // 7. Inject header into the page
  headerDiv.innerHTML = headerContent;

  // 8. Attach event listeners to dynamically created buttons
  attachHeaderButtonListeners();
}

// Attach event listeners for dynamically created buttons
function attachHeaderButtonListeners() {
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", logout);
  }

  const logoutPatientBtn = document.getElementById("logoutPatientBtn");
  if (logoutPatientBtn) {
    logoutPatientBtn.addEventListener("click", logoutPatient);
  }

  const patientLogin = document.getElementById("patientLogin");
  if (patientLogin) {
    patientLogin.addEventListener("click", () => openModal("patientLogin"));
  }

  const patientSignup = document.getElementById("patientSignup");
  if (patientSignup) {
    patientSignup.addEventListener("click", () => openModal("patientSignup"));
  }

  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => openModal("addDoctor"));
  }
}

// Logout function for admin/doctor
function logout() {
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");
  window.location.href = "/";
}

// Logout function for logged patients
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient"); // revert role to 'patient'
  window.location.href = "/pages/patientDashboard.html";
}

// Initialize header on page load
renderHeader();