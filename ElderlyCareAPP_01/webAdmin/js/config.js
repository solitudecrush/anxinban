// ==================== API Configuration ====================

const API_CONFIG = {
  // 后端接口基地址，可在此修改
  BASE_URL: 'http://localhost:8080',
  // 接口前缀
  API_PREFIX: '/api',
  // 完整基础路径
  get API_BASE() {
    return this.BASE_URL + this.API_PREFIX;
  }
};

// 获取完整 API URL
function apiUrl(path) {
  // 如果 path 已包含 /api 前缀，直接拼接 BASE_URL
  if (path.startsWith('/api')) {
    return API_CONFIG.BASE_URL + path;
  }
  // 否则拼接完整前缀
  return API_CONFIG.API_BASE + (path.startsWith('/') ? path : '/' + path);
}
