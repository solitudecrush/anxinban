// ==================== Auth Module ====================

function handleLogin(e) {
  e.preventDefault();
  const username = document.getElementById("username").value.trim();
  const password = document.getElementById("password").value.trim();

  const staff = MOCK_STAFF.find(s => s.username === username && s.password === password);
  if (staff) {
    localStorage.setItem("is_logged_in", "true");
    localStorage.setItem("current_user", staff.username);
    localStorage.setItem("current_staff", JSON.stringify(staff));
    ensureMockData();
    addAuditLog("登录系统", `工作人员 ${staff.name}（${staff.role}）登录系统`);
    window.location.href = "dashboard.html";
  } else {
    const msg = document.getElementById("login-msg");
    msg.textContent = "账号或密码错误，演示账号：shequ01 / 123456";
    msg.style.color = "#ff6b6b";
  }
}

if (document.getElementById("login-form")) {
  document.getElementById("login-form").addEventListener("submit", handleLogin);
}
