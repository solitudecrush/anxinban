// ==================== Utility Functions ====================

function maskName(name) {
  return name;
}

function formatDateTime(d = new Date()) {
  const pad = n => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
}

function getCurrentUser() {
  const staff = getCurrentStaff();
  return staff ? staff.name : (localStorage.getItem("current_user") || "shequ01");
}

function getCurrentStaff() {
  const saved = localStorage.getItem("current_staff");
  if (saved) {
    try {
      return JSON.parse(saved);
    } catch (e) {
      return null;
    }
  }
  return null;
}

// ==================== Cross-Page Sync ====================

function broadcastChange(key) {
  const triggerKey = "sync_trigger_" + key;
  const ts = Date.now();
  try {
    localStorage.setItem(triggerKey, String(ts));
  } catch (e) {}
}

function syncWatch(keys, callback) {
  if (typeof keys === "string") keys = [keys];
  const handler = (e) => {
    if (!e.key) return;
    const realKey = e.key.startsWith("sync_trigger_") ? e.key.replace("sync_trigger_", "") : e.key;
    if (keys.includes(realKey)) {
      callback(realKey);
    }
  };
  window.addEventListener("storage", handler);
  return handler;
}

function startAutoRefresh(renderFn, intervalMs = 3000) {
  return setInterval(renderFn, intervalMs);
}

function getBoundElderId() {
  return 100;
}

// ==================== Audit Log ====================

function addAuditLog(action, detail, snapshot = null) {
  let logs = JSON.parse(localStorage.getItem("audit_logs") || "[]");
  const staff = getCurrentStaff();
  const userName = staff ? staff.name : (localStorage.getItem("current_user") || "shequ01");
  const entry = {
    id: Date.now(),
    user: userName,
    time: formatDateTime(),
    action: action,
    detail: detail
  };
  if (snapshot) entry.snapshot = snapshot;
  logs.unshift(entry);
  if (logs.length > 50) logs = logs.slice(0, 50);
  localStorage.setItem("audit_logs", JSON.stringify(logs));
  broadcastChange("audit_logs");
}

// ==================== App Notifications (保留本地，用于 Web→App 通知演示) ====================

function notifyApp(notification) {
  const list = getAppNotifications();
  list.unshift({
    id: Date.now(),
    ...notification,
    time: formatDateTime(),
    read: false
  });
  saveAppNotifications(list.slice(0, 100));
}

// ==================== Intrusion Simulation ====================

function simulateIntrusion(buildingName, elderId, elderName, room) {
  const alerts = getIntrusionAlerts();
  const now = formatDateTime();
  const id = 1000 + alerts.length + 1;
  const snapshot = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='320' height='240'%3E%3Crect fill='%23222' width='320' height='240'/%3E%3Ccircle cx='160' cy='80' r='30' fill='%23555'/%3E%3Crect x='130' y='110' width='60' height='90' fill='%23444'/%3E%3Ctext x='160' y='220' text-anchor='middle' fill='%23888' font-size='14'%3E危险人员快照（模拟）%3C/text%3E%3C/svg%3E";
  const alert = {
    id: id,
    elderId: elderId || 1,
    elderName: elderName || "未知",
    building: buildingName,
    unit: "1单元",
    room: room || buildingName + " " + (Math.floor(Math.random() * 6) + 1) + "0" + (Math.floor(Math.random() * 9) + 1),
    time: now,
    status: "pending",
    location: (buildingName + " " + (room || "客厅")),
    snapshot: snapshot
  };
  alerts.unshift(alert);
  saveIntrusionAlerts(alerts);

  const boundId = getBoundElderId();
  const isTargetElder = (elderId === boundId);
  notifyApp({
    type: "intrusion",
    title: "陌生人闯入告警",
    content: buildingName + " " + alert.room + " 检测到陌生人闯入，请立即关注" + (isTargetElder ? "（您绑定的老人曾姐所在楼栋）" : ""),
    building: buildingName,
    room: alert.room,
    elderId: elderId || 0,
    snapshot: snapshot
  });

  setTimeout(() => {
    alert("【社区工作人员通知】\n" + buildingName + " " + alert.room + " 发生陌生人闯入告警！\n抓拍时间：" + now);
  }, 300);

  addAuditLog("陌生人闯入告警", buildingName + " " + alert.room + " 检测到陌生人闯入，抓拍时间：" + now, snapshot);

  return alert;
}

function getAuditLogs() {
  return JSON.parse(localStorage.getItem("audit_logs") || "[]");
}

function isCameraAuthValid(elder) {
  if (!elder.hasCamera) return false;
  if (!elder.cameraAuthUntil || elder.cameraAuthUntil === 0) return false;
  return Date.now() < elder.cameraAuthUntil;
}

function clearExpiredCameraAuth() {
  const elderly = getElderly();
  let changed = false;
  for (let e of elderly) {
    if (e.cameraAuthUntil && e.cameraAuthUntil > 0 && Date.now() >= e.cameraAuthUntil) {
      e.cameraAuthUntil = 0;
      e.cameraPending = false;
      changed = true;
    }
  }
  if (changed) saveElderly(elderly);
}

// ==================== Data Initialization (已废弃，保留空函数兼容旧页面) ====================

function ensureMockData() {
  // 不再初始化 Mock 数据，数据改为从后端 API 加载
  // 保留此函数以避免旧页面报错
}

function checkAuth() {
  if (!localStorage.getItem("is_logged_in")) {
    window.location.href = "index.html";
  }
}

function logout() {
  addAuditLog("退出登录", `工作人员 ${getCurrentUser()} 退出系统`);
  // 调用后端退出（异步，不等待）
  try { AuthAPI.logout(); } catch (e) {}
  localStorage.removeItem("is_logged_in");
  localStorage.removeItem("current_user");
  localStorage.removeItem("current_staff");
  localStorage.removeItem("access_token");
  window.location.href = "index.html";
}

function initSidebar(activePage) {
  const nav = document.querySelector(".sidebar-nav");
  if (!nav) return;
  const items = [
    { id: "dashboard", label: "数据驾驶舱", icon: "dashboard", href: "dashboard.html" },
    { id: "3d_map", label: "3D 社区全景", icon: "map", href: "3d_map.html" },
    { id: "elderly", label: "老人管理", icon: "elderly", href: "elderly.html" },
    { id: "workorders", label: "工单管理", icon: "assignment", href: "workorders.html" },
    { id: "auditlog", label: "操作日志", icon: "history", href: "auditlog.html" }
  ];
  nav.innerHTML = items.map(it => `
    <a href="${it.href}" class="nav-item ${it.id === activePage ? "active" : ""}">
      <span class="nav-icon"><i class="material-icons" style="font-size:18px;">${it.icon}</i></span>
      <span class="nav-label">${it.label}</span>
    </a>
  `).join("");

  const staff = getCurrentStaff();
  const userNameEl = document.querySelector(".user-name");
  const userRoleEl = document.querySelector(".user-role");
  if (userNameEl && staff) userNameEl.textContent = staff.name;
  if (userRoleEl && staff) userRoleEl.textContent = staff.role;
}
