const API_BASE = "http://localhost:8080";
console.log("JS loaded");

console.log("JS loaded");

const regEmail = document.getElementById("regEmail");
const regPassword = document.getElementById("regPassword");
const loginEmail = document.getElementById("loginEmail");
const loginPassword = document.getElementById("loginPassword");
const fileInput = document.getElementById("fileInput");

// expose functions for inline onclick
window.register = register;
window.login = login;
window.uploadFile = uploadFile;
window.loadFiles = loadFiles;


function saveToken(token) {
  localStorage.setItem("jwt", token);
}

function getToken() {
  return localStorage.getItem("jwt");
}

async function register() {
  console.log("Registering...");
  const email = regEmail.value;
  const password = regPassword.value;

  await fetch(`${API_BASE}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password })
  });

  alert("Registered");
}

async function login() {
  const email = loginEmail.value;
  const password = loginPassword.value;

  const res = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password })
  });

  const token = await res.text();
  saveToken(token);
  alert("Logged in");
}

async function uploadFile() {
  const file = fileInput.files[0];
  if (!file) return alert("Select a file");

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
  const res = await fetch(`${API_BASE}/files`, {
    headers: {
      "Authorization": `Bearer ${getToken()}`
    }
  });

  const files = await res.json();
  const list = document.getElementById("fileList");
  list.innerHTML = "";

  files.forEach(f => {
    const li = document.createElement("li");
    li.textContent = `${f.fileName} (${f.status})`;
    list.appendChild(li);
  });
}
