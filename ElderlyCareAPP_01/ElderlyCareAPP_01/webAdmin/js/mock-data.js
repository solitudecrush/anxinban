// ==================== Data Cache Layer (替代 localStorage Mock) ====================
// 本文件保留旧版 getXxx / saveXxx 函数签名，供页面代码最小改动迁移。
// 底层数据改为内存缓存，通过 loadXxx() 异步函数从后端 API 加载。

// ---------- 全局内存缓存 ----------
const _cache = {
  elderly: [],
  alerts: [],
  devices: [],
  workOrders: [],
  familyRequests: [],
  intrusionAlerts: [],
  cameraRequests: [],
  appNotifications: [],
  staff: [],
  stats: null,
  buildings: [],
};

// ---------- 兼容旧页面的同步读取函数 ----------

function getStaff() {
  return _cache.staff;
}

function getElderly() {
  return _cache.elderly;
}

function getAlerts() {
  return _cache.alerts;
}

function getDevices() {
  return _cache.devices;
}

function getWorkOrders() {
  return _cache.workOrders;
}

function getFamilyRequests() {
  return _cache.familyRequests;
}

function getIntrusionAlerts() {
  return _cache.intrusionAlerts;
}

function getCameraRequests() {
  return _cache.cameraRequests;
}

function getAppNotifications() {
  return _cache.appNotifications;
}

// ---------- 兼容旧页面的同步写入函数 ----------
// 仅更新内存缓存并触发跨页同步，不再持久化到 localStorage

function saveElderly(data) {
  _cache.elderly = data;
  broadcastChange("elderly_data");
}

function saveAlerts(data) {
  _cache.alerts = data;
  broadcastChange("alerts_data");
}

function saveDevices(data) {
  _cache.devices = data;
  broadcastChange("devices_data");
}

function saveWorkOrders(data) {
  _cache.workOrders = data;
  broadcastChange("workorders_data");
}

function saveFamilyRequests(data) {
  _cache.familyRequests = data;
  broadcastChange("family_requests");
}

function saveIntrusionAlerts(data) {
  _cache.intrusionAlerts = data;
  broadcastChange("intrusion_alerts");
}

function saveCameraRequests(data) {
  _cache.cameraRequests = data;
  broadcastChange("camera_requests");
}

function saveAppNotifications(data) {
  _cache.appNotifications = data;
  broadcastChange("app_notifications");
}

// ---------- 统计数据（兼容旧 getStats） ----------

function getStats() {
  const elderly = getElderly();
  const alerts = getAlerts();
  const devices = getDevices();
  const orders = getWorkOrders();
  const intrusions = getIntrusionAlerts();
  const todayStr = formatDateTime().slice(0, 10);
  return {
    totalElderly: elderly.length,
    todayAlerts: alerts.filter(a => {
      const t = (a.time || a.occurTime || '').slice(0, 10);
      return t === todayStr && a.status === 'pending';
    }).length,
    todayIntrusions: intrusions.filter(a => {
      const t = (a.time || '').slice(0, 10);
      return t === todayStr;
    }).length,
    onlineDevices: devices.filter(d => d.online !== false && d.status === 'online').length,
    pendingOrders: orders.filter(o => o.status === '待分配' || o.status === '处理中' || o.status === 'pending').length,
    abnormalHealth: elderly.filter(e => e.status !== 'normal').length,
  };
}

// ---------- 异步加载函数（从后端 API 刷新缓存） ----------

async function loadStaff() {
  try {
    const data = await StaffAPI.list();
    _cache.staff = Array.isArray(data) ? data : [];
    return _cache.staff;
  } catch (e) {
    console.error('加载工作人员失败', e);
    return _cache.staff;
  }
}

async function loadElderly(params = {}) {
  try {
    const result = await ElderAPI.list(params);
    const list = result?.list || result || [];
    _cache.elderly = list.map(mapElderDto);
    return _cache.elderly;
  } catch (e) {
    console.error('加载老人列表失败', e);
    return _cache.elderly;
  }
}

async function loadAlerts(params = {}) {
  try {
    const result = await AlarmAPI.list(params);
    const list = result?.list || result || [];
    _cache.alerts = list.map(mapAlarmDto);
    return _cache.alerts;
  } catch (e) {
    console.error('加载告警列表失败', e);
    return _cache.alerts;
  }
}

async function loadDevices(params = {}) {
  // 设备列表接口在文档中为 /api/device/list
  try {
    const result = await request('/device/list' + getQueryString(params));
    const list = result?.list || result || [];
    _cache.devices = list.map(mapDeviceDto);
    return _cache.devices;
  } catch (e) {
    console.error('加载设备列表失败', e);
    return _cache.devices;
  }
}

async function loadWorkOrders(params = {}) {
  try {
    const result = await WorkOrderAPI.list(params);
    const list = result?.list || result || [];
    _cache.workOrders = list.map(mapWorkOrderDto);
    return _cache.workOrders;
  } catch (e) {
    console.error('加载工单列表失败', e);
    return _cache.workOrders;
  }
}

async function loadFamilyRequests(params = {}) {
  try {
    const result = await ServiceRequestAPI.list(params);
    const list = result?.list || result || [];
    _cache.familyRequests = list.map(mapServiceRequestDto);
    return _cache.familyRequests;
  } catch (e) {
    console.error('加载服务申请失败', e);
    return _cache.familyRequests;
  }
}

async function loadIntrusionAlerts(params = {}) {
  try {
    const result = await AlarmAPI.intrusionList(params);
    const list = result?.list || result || [];
    _cache.intrusionAlerts = list.map(mapAlarmDto);
    return _cache.intrusionAlerts;
  } catch (e) {
    console.error('加载闯入告警失败', e);
    return _cache.intrusionAlerts;
  }
}

async function loadCameraRequests(staffId) {
  try {
    const result = await MonitorRequestAPI.listStaff(staffId);
    const list = Array.isArray(result) ? result : [];
    _cache.cameraRequests = list.map(mapMonitorRequestDto);
    return _cache.cameraRequests;
  } catch (e) {
    console.error('加载监控申请失败', e);
    return _cache.cameraRequests;
  }
}

async function loadDashboardStats() {
  try {
    const data = await DashboardAPI.stats();
    _cache.stats = data;
    return data;
  } catch (e) {
    console.error('加载看板统计失败', e);
    return _cache.stats;
  }
}

async function loadBuildings() {
  try {
    const data = await DashboardAPI.buildings();
    _cache.buildings = Array.isArray(data) ? data : [];
    return _cache.buildings;
  } catch (e) {
    console.error('加载楼栋列表失败', e);
    return _cache.buildings;
  }
}

// ---------- 一键刷新所有数据 ----------

async function refreshAllData() {
  await Promise.all([
    loadElderly(),
    loadAlerts(),
    loadDevices(),
    loadWorkOrders(),
    loadFamilyRequests(),
    loadIntrusionAlerts(),
    loadStaff(),
    loadDashboardStats(),
    loadBuildings(),
  ]);
}
