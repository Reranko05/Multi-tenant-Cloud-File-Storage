const API_BASE = "http://localhost:8080";
console.log("JS loaded");

/* =======================
   DOM elements
   (exist depending on page)
======================= */
const regEmail = document.getElementById("regEmail");
const regPassword = document.getElementById("regPassword");

const loginEmail = document.getElementById("loginEmail");
const loginPassword = document.getElementById("loginPassword");

const fileInput = document.getElementById("fileInput");
const fileList = document.getElementById("fileList");

/* =======================
   Expose functions
======================= */
window.register = register;
window.login = login;
window.uploadFile = uploadFile;
window.loadFiles = loadFiles;
window.logout = logout;
window.viewFile = viewFile;


/* =======================
   Auth helpers
======================= */

function saveToken(token) {
  localStorage.setItem("jwt", token);
}

function getToken() {
  return localStorage.getItem("jwt");
}

function logout() {
  localStorage.removeItem("jwt");
  window.location.href = "./login.html";
}

function requireAuth() {
  if (!getToken()) {
    window.location.href = "./login.html";
  }
}

/* =======================
   Auth actions
======================= */

async function register() {
  if (!regEmail || !regPassword) return;

  const res = await fetch(`${API_BASE}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email: regEmail.value,
      password: regPassword.value
    })
  });

  if (!res.ok) {
    alert("Registration failed");
    return;
  }

  alert("Registered successfully. Please login.");
}

async function login() {
  if (!loginEmail || !loginPassword) return;

  const res = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email: loginEmail.value,
      password: loginPassword.value
    })
  });

  if (!res.ok) {
    alert("Invalid credentials");
    return;
  }

  const token = await res.text();
  saveToken(token);
  window.location.href = "./drive.html";
}

/* =======================
   File actions
======================= */

async function uploadFile() {
  if (!fileInput) return;

  const file = fileInput.files[0];
  if (!file) {
    alert("Please select a file");
    return;
  }

  // 1️⃣ Create upload intent
  const intentRes = await fetch(`${API_BASE}/files/upload-intent`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${getToken()}`
    },
    body: JSON.stringify({
      fileName: file.name,
      fileSize: file.size,
      contentType: file.type
    })
  });

  if (!intentRes.ok) {
    alert("Failed to create upload intent");
    return;
  }

  const { fileId, uploadUrl } = await intentRes.json();

  // 2️⃣ Upload directly to S3
  await fetch(uploadUrl, {
    method: "PUT",
    headers: { "Content-Type": file.type },
    body: file
  });

  // 3️⃣ Mark upload complete
  await fetch(`${API_BASE}/files/${fileId}/complete`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${getToken()}`
    }
  });

  alert("Upload complete");
  loadFiles();
}

async function loadFiles() {
  if (!fileList) return;

  const res = await fetch(`${API_BASE}/files`, {
    headers: {
      "Authorization": `Bearer ${getToken()}`
    }
  });

  const files = await res.json();
  fileList.innerHTML = "";

  files.forEach(f => {
    const li = document.createElement("li");

    const nameSpan = document.createElement("span");
    nameSpan.textContent = `${f.fileName} (${f.status}) `;

    const viewBtn = document.createElement("button");
    viewBtn.textContent = "View";
    viewBtn.onclick = () => viewFile(f.fileId);

    li.appendChild(nameSpan);
    li.appendChild(viewBtn);
    fileList.appendChild(li);
  });
}

async function viewFile(fileId) {
  const res = await fetch(`${API_BASE}/files/${fileId}/view`, {
    headers: {
      "Authorization": `Bearer ${getToken()}`
    }
  });

  if (!res.ok) {
    alert("Unable to view file");
    return;
  }

  const viewUrl = await res.text();

  // Open in new tab (Google Drive style)
  window.open(viewUrl, "_blank");
}


/* =======================
   Auto-protect drive page
======================= */
if (fileInput || fileList) {
  requireAuth();
}
