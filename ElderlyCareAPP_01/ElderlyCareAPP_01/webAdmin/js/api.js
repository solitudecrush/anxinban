// ==================== Unified API Service Layer ====================
// 所有后端接口调用统一在此封装，替代原有的 mock-data.js 本地存储方案

// ---------- 基础请求封装 ----------

async function request(url, options = {}) {
  const fullUrl = url.startsWith('http') ? url : apiUrl(url);

  const defaultHeaders = {
    'Content-Type': 'application/json',
  };

  // 若 localStorage 中有用户令牌，可在此注入 Authorization（当前后端未强制校验）
  const token = localStorage.getItem('access_token');
  if (token) {
    defaultHeaders['Authorization'] = 'Bearer ' + token;
  }

  const fetchOptions = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...(options.headers || {}),
    },
  };

  if (options.body && typeof options.body === 'object' && !(options.body instanceof FormData)) {
    fetchOptions.body = JSON.stringify(options.body);
  }

  try {
    const response = await fetch(fullUrl, fetchOptions);
    const result = await response.json();

    if (result.code !== 200 && result.code !== 201) {
      const msg = result.message || '请求失败';
      throw new Error(msg);
    }
    return result.data;
  } catch (err) {
    if (err.message === 'Failed to fetch') {
      throw new Error('无法连接到后端服务，请确认后端已启动');
    }
    throw err;
  }
}

function getQueryString(params) {
  if (!params) return '';
  const qs = new URLSearchParams();
  for (const [key, value] of Object.entries(params)) {
    if (value !== undefined && value !== null && value !== '') {
      qs.append(key, String(value));
    }
  }
  const str = qs.toString();
  return str ? '?' + str : '';
}

// ---------- 认证模块 ----------

const AuthAPI = {
  login(phone, password) {
    return request('/auth/login', {
      method: 'POST',
      body: { phone, password, userType: 'staff' },
    });
  },

  register(data) {
    return request('/auth/register', {
      method: 'POST',
      body: { ...data, userType: 'staff' },
    });
  },

  resetPassword(phone, newPassword) {
    return request('/auth/reset-password', {
      method: 'POST',
      body: { phone, newPassword },
    });
  },

  logout() {
    return request('/auth/logout', { method: 'POST' });
  },

  me(phone) {
    return request('/auth/me' + getQueryString({ phone }));
  },
};

// ---------- 老人管理模块 ----------

const ElderAPI = {
  list(params = {}) {
    // params: { name, building, roomNumber, healthStatus, page, pageSize }
    return request('/elder/list' + getQueryString(params));
  },

  detail(elderId) {
    return request('/elder/' + elderId);
  },

  create(data) {
    return request('/elder', { method: 'POST', body: data });
  },

  update(elderId, data) {
    return request('/elder/' + elderId, { method: 'PUT', body: data });
  },

  remove(elderId) {
    return request('/elder/' + elderId, { method: 'DELETE' });
  },
};

// ---------- 健康数据模块 ----------

const HealthAPI = {
  latest(elderId) {
    return request('/health/latest/' + elderId);
  },

  trend(elderId, type, period = 'week') {
    return request('/health/trend/' + elderId + getQueryString({ type, period }));
  },

  analysis(elderId, period = 'week') {
    return request('/health/analysis/' + elderId + getQueryString({ period }));
  },
};

// ---------- 告警管理模块 ----------

const AlarmAPI = {
  list(params = {}) {
    // params: { elderId, deviceId, alarmType, status, startTime, endTime, page, pageSize }
    return request('/alarm/list' + getQueryString(params));
  },

  intrusionList(params = {}) {
    // params: { status, building, page, pageSize }
    return request('/alarm/intrusion/list' + getQueryString(params));
  },

  resolve(alarmId, handler, handleTime, remark) {
    return request('/alarm/' + alarmId + '/resolve', {
      method: 'PUT',
      body: { handler, handleTime, remark },
    });
  },

  read(alarmId) {
    return request('/alarm/' + alarmId + '/read', { method: 'PUT' });
  },
};

// ---------- 工单管理模块 ----------

const WorkOrderAPI = {
  list(params = {}) {
    // params: { keyword, elderName, status, page, pageSize }
    return request('/work-order/list' + getQueryString(params));
  },

  create(data) {
    return request('/work-order', { method: 'POST', body: data });
  },

  updateStatus(orderId, status) {
    return request('/work-order/' + orderId + '/status', {
      method: 'PUT',
      body: { status },
    });
  },

  assign(orderId, handlerId, handlerName) {
    return request('/work-order/' + orderId + '/assign', {
      method: 'PUT',
      body: { handlerId, handlerName },
    });
  },
};

// ---------- 服务申请模块 ----------

const ServiceRequestAPI = {
  list(params = {}) {
    // params: { requestType, status, page, pageSize }
    return request('/service-request/list' + getQueryString(params));
  },

  convert(requestId, orderId) {
    return request('/service-request/' + requestId + '/convert', {
      method: 'POST',
      body: { orderId },
    });
  },

  reject(requestId, reason) {
    return request('/service-request/' + requestId + '/reject', {
      method: 'POST',
      body: { reason },
    });
  },
};

// ---------- 监控申请模块 ----------

const MonitorRequestAPI = {
  create(data) {
    return request('/monitor-request', { method: 'POST', body: data });
  },

  listStaff(staffId) {
    return request('/monitor-request/list/staff' + getQueryString({ staffId }));
  },

  check(elderId, staffId) {
    return request('/monitor-request/check' + getQueryString({ elderId, staffId }));
  },
};

// ---------- 社区看板模块 ----------

const DashboardAPI = {
  stats() {
    return request('/dashboard/stats');
  },

  buildings() {
    return request('/dashboard/buildings');
  },
};

// ---------- 工作人员模块 ----------

const StaffAPI = {
  list(communityId) {
    return request('/staff/list' + getQueryString({ communityId }));
  },

  create(data) {
    return request('/staff', { method: 'POST', body: data });
  },

  update(staffId, data) {
    return request('/staff/' + staffId, { method: 'PUT', body: data });
  },

  remove(staffId) {
    return request('/staff/' + staffId, { method: 'DELETE' });
  },
};

// ---------- 通知模块 ----------

const NotificationAPI = {
  list(userId, userType = 'staff', page, pageSize) {
    return request('/notification/list' + getQueryString({ userId, userType, page, pageSize }));
  },

  read(notificationId) {
    return request('/notification/' + notificationId + '/read', { method: 'POST' });
  },

  unreadCount(userId, userType = 'staff') {
    return request('/notification/unread-count' + getQueryString({ userId, userType }));
  },
};

// ---------- 兼容性辅助函数（供旧页面过渡使用） ----------

// 将后端 ElderDto 映射为前端旧格式（减少页面改动量）
function mapElderDto(e) {
  if (!e) return null;
  return {
    id: e.elderId,
    name: e.name,
    age: e.age,
    gender: e.gender,
    building: e.building,
    room: e.roomNumber,
    status: e.healthStatus,
    health: e.healthStatusText || (e.healthStatus === 'normal' ? '正常' : e.healthStatus === 'warning' ? '关注' : '高危'),
    phone: e.phone,
    contact: e.guardianPhone || e.familyPhone,
    contactPhone: e.familyPhone,
    hasCamera: e.hasCamera,
    cameraAuthUntil: e.cameraAuthUntil || 0,
    cameraPending: e.cameraPending || false,
    bp: e.bp || (e.systolic && e.diastolic ? e.systolic + '/' + e.diastolic : '130/85'),
    hr: e.heartRate || 72,
    spo2: e.bloodOxygen || 98,
    temp: e.temperature || 36.5,
    insomnia: e.insomnia || '无',
    sleepTime: e.sleepTime || '22:00',
    lastOnline: e.lastOnline || formatDateTime(),
    address: e.address,
    healthNote: e.healthNote,
    avatar: e.avatar,
    tags: e.tags || [],
  };
}

// 将前端旧格式映射为后端 ElderDto
function toElderDto(e) {
  return {
    elderId: e.id ? String(e.id) : undefined,
    name: e.name,
    age: e.age,
    gender: e.gender,
    building: e.building,
    roomNumber: e.room,
    phone: e.phone,
    guardianPhone: e.contactPhone,
    familyPhone: e.contactPhone,
    healthStatus: e.status,
    address: e.address,
    healthNote: e.healthNote,
    hasCamera: e.hasCamera,
  };
}

// 将后端 AlarmDto 映射为前端旧格式
function mapAlarmDto(a) {
  if (!a) return null;
  return {
    id: a.alarmId,
    elderId: a.elderId,
    elderName: a.elderName,
    type: a.alarmType,
    time: a.occurTime || a.createTime,
    status: a.status,
    location: a.building && a.roomNumber ? a.building + ' ' + a.roomNumber + ' ' + (a.unit || '') : a.description,
    handler: a.handlerName || a.handler,
    handleTime: a.handleTime,
    severity: a.severity,
    snapshotUrl: a.snapshotUrl,
    description: a.description,
  };
}

// 将后端 WorkOrderDto 映射为前端旧格式
function mapWorkOrderDto(o) {
  if (!o) return null;
  return {
    id: o.orderId,
    elderId: o.elderId,
    elderName: o.elderName,
    type: o.orderType,
    createTime: o.createTime,
    status: o.status,
    assignee: o.handlerName,
    assigneePhone: o.handlerPhone,
    description: o.description,
    fromFamily: !!o.serviceRequestId,
    finishTime: o.completeTime,
    serviceRequestId: o.serviceRequestId,
  };
}

// 将后端 ServiceRequestDto 映射为前端旧格式
function mapServiceRequestDto(r) {
  if (!r) return null;
  return {
    id: r.requestId,
    elderId: r.elderId,
    elderName: r.elderName,
    familyName: r.familyName,
    familyPhone: r.familyPhone,
    type: r.requestType,
    content: r.content,
    requestTime: r.createTime,
    status: r.status,
    convertedTo: r.relatedOrderId,
    rejectReason: r.rejectReason,
  };
}

// 将后端 DeviceDto 映射为前端旧格式
function mapDeviceDto(d) {
  if (!d) return null;
  return {
    id: d.deviceId,
    name: d.deviceName,
    type: d.deviceType,
    building: d.building,
    room: d.room,
    online: d.status === 'online',
    battery: d.batteryLevel || 100,
  };
}

// 将后端 MonitorRequestDto 映射为前端旧格式
function mapMonitorRequestDto(m) {
  if (!m) return null;
  return {
    id: m.requestId,
    elderId: m.elderId,
    elderName: m.elderName,
    staffName: m.staffName,
    staffPhone: m.staffPhone,
    reason: m.reason,
    requestTime: m.createTime,
    status: m.status,
    expiresAt: m.expiredAt ? new Date(m.expiredAt).getTime() : 0,
    approvedAt: m.approvedAt ? new Date(m.approvedAt).getTime() : 0,
  };
}

// 将后端 NotificationDto 映射为前端旧格式
function mapNotificationDto(n) {
  if (!n) return null;
  return {
    id: n.notificationId,
    type: n.notificationType,
    title: n.title,
    content: n.content,
    time: n.createTime,
    read: n.isRead,
    building: n.building,
    room: n.room,
    orderId: n.orderId,
    requestId: n.requestId,
    elderId: n.elderId,
  };
}
