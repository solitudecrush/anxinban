# 安心伴云端后端运维手册 (Cloud Operation Runbook)

> 最后更新：2026-06-19
> 适用环境：阿里云 ECS `120.27.129.78`，操作系统 Ubuntu 22.04

---

## 1. 当前云端服务架构

```
┌─────────────────────────────────────────────────────┐
│                   公网入口                            │
│              http://120.27.129.78:8080               │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│        Java Spring Boot 后端 (:8080)                  │
│        systemd: anxinban-backend.service             │
│        目录: /var/www/anxinban-backend                │
│        JAR: target/anxinban-0.0.1-SNAPSHOT.jar       │
└──────────┬──────────────────────┬───────────────────┘
           │ 内部调用 (127.0.0.1)  │
           ▼                      ▼
┌──────────────────────┐  ┌──────────────────────────┐
│ Python AI 服务 (:8000)│  │     MySQL (localhost)     │
│ systemd: yizhi-ai     │  │     数据库: anxinban       │
│ 目录: /var/www/       │  │     端口: 3306            │
│       yizhi-ai-module │  └──────────────────────────┘
└──────────┬───────────┘
           │ HTTPS
           ▼
┌──────────────────────┐
│   DeepSeek API       │
│   (大模型推理)        │
└──────────────────────┘
```

**关键调用链：**

1. 前端/硬件 → `POST :8080/api/device/upload` → Java 后端
2. Java 后端 → `POST http://127.0.0.1:8000/api/ai/health-analysis` → Python AI
3. Python AI → DeepSeek API → 返回分析结果
4. Java 后端 → MySQL → 保存 sensor_data、alarm_event、ai_analysis_record、work_order

---

## 2. Java 后端管理

### 2.1 服务信息

| 项目 | 值 |
|------|-----|
| 服务名 | `anxinban-backend.service` |
| 端口 | 8080 |
| 工作目录 | `/var/www/anxinban-backend` |
| JAR 路径 | `/var/www/anxinban-backend/target/anxinban-0.0.1-SNAPSHOT.jar` |
| 环境变量 | `AI_SERVICE_BASE_URL=http://127.0.0.1:8000` |

### 2.2 启动/停止/重启

```bash
# 查看状态
sudo systemctl status anxinban-backend

# 启动
sudo systemctl start anxinban-backend

# 停止
sudo systemctl stop anxinban-backend

# 重启
sudo systemctl restart anxinban-backend

# 查看实时日志
sudo journalctl -u anxinban-backend -f

# 查看最近 100 行日志
sudo journalctl -u anxinban-backend -n 100

# 查看今日日志
sudo journalctl -u anxinban-backend --since today
```

### 2.3 开机自启

```bash
# 启用开机自启
sudo systemctl enable anxinban-backend

# 禁用开机自启
sudo systemctl disable anxinban-backend

# 检查是否已启用
systemctl is-enabled anxinban-backend
```

### 2.4 手动启动（备用方式）

如果 systemd 不可用，可以手动启动：

```bash
cd /var/www/anxinban-backend
nohup /usr/bin/java -jar target/anxinban-0.0.1-SNAPSHOT.jar \
  --server.port=8080 \
  --ai.service.base-url=http://127.0.0.1:8000 \
  > /var/log/anxinban-backend.log 2>&1 &
```

---

## 3. Python AI 服务管理

### 3.1 服务信息

| 项目 | 值 |
|------|-----|
| 服务名 | `yizhi-ai.service` |
| 端口 | 8000（仅监听 127.0.0.1） |
| 工作目录 | `/var/www/yizhi-ai-module` |
| 启动命令 | `python3 -m mock_cloud.app --host 127.0.0.1 --port 8000` |
| 环境文件 | `/var/www/yizhi-ai-module/.env` |

### 3.2 启动/停止/重启

```bash
# 查看状态
sudo systemctl status yizhi-ai

# 启动
sudo systemctl start yizhi-ai

# 停止
sudo systemctl stop yizhi-ai

# 重启
sudo systemctl restart yizhi-ai

# 查看实时日志
sudo journalctl -u yizhi-ai -f

# 查看最近日志
sudo journalctl -u yizhi-ai -n 100
```

### 3.3 启动顺序

Java 后端依赖 Python AI 和 MySQL，systemd 已配置启动顺序：

```
MySQL → yizhi-ai → anxinban-backend
```

---

## 4. MySQL 检查方式

### 4.1 服务状态

```bash
# 检查 MySQL 是否运行
sudo systemctl status mysql
# 或
sudo systemctl status mysqld
```

### 4.2 连接测试

```bash
# 本地连接测试
mysql -uroot -p<密码> -e "SELECT 1;"

# 查看数据库
mysql -uroot -p<密码> -e "SHOW DATABASES;"

# 查看表
mysql -uroot -p<密码> -e "USE anxinban; SHOW TABLES;"
```

### 4.3 关键表检查

```bash
# 检查老人数据
mysql -uroot -p<密码> -e "SELECT COUNT(*) FROM anxinban.elder_user;"

# 检查告警数据
mysql -uroot -p<密码> -e "SELECT COUNT(*) FROM anxinban.alarm_event;"

# 检查 AI 分析记录
mysql -uroot -p<密码> -e "SELECT COUNT(*) FROM anxinban.ai_analysis_record;"

# 检查工单数据
mysql -uroot -p<密码> -e "SELECT COUNT(*) FROM anxinban.work_order;"
```

### 4.4 端口检查

```bash
sudo ss -tlnp | grep 3306
```

---

## 5. DeepSeek 可用性检查

### 5.1 通过 Java 后端检查

```bash
# 查看 AI 服务整体状态
curl http://120.27.129.78:8080/api/ai/service-status
```

返回示例：
```json
{
  "code": 200,
  "data": {
    "python_ai_reachable": true,
    "current_mode": "python_ai_service",
    "source": "python_ai_service"
  }
}
```

- `python_ai_reachable: true` → Python AI 正常
- `current_mode: python_ai_service` → 正在使用 DeepSeek
- `current_mode: java_fallback` → Python AI 不可用，已回退到 Java 规则引擎

### 5.2 直接测试 Python AI

```bash
# 健康检查
curl http://127.0.0.1:8000/api/health

# 测试 AI 分析
curl -X POST http://127.0.0.1:8000/api/ai/health-analysis \
  -H "Content-Type: application/json" \
  -d '{"elder_id":"elder_001","recent_health":{"heart_rate":125,"spo2":88,"temperature":38.5}}'
```

---

## 6. 比赛前检查清单

依次执行以下命令，确认全部返回正常：

### 6.1 基础健康检查

```bash
# 1. Java 后端健康
curl http://120.27.129.78:8080/api/health
# 期望: {"code":200, "data":{"status":"running","database":"connected"}}

# 2. AI 服务状态
curl http://120.27.129.78:8080/api/ai/service-status
# 期望: current_mode=python_ai_service, python_ai_reachable=true
```

### 6.2 核心业务接口

```bash
# 3. AI 健康分析
curl -X POST http://120.27.129.78:8080/api/ai/health-analysis \
  -H "Content-Type: application/json" \
  -d '{"elder_id":"elder_001","recent_health":{"heart_rate":125,"spo2":88,"temperature":38.5}}'
# 期望: source=python_ai_service, risk_level=高风险

# 4. 设备数据上传
curl -X POST http://120.27.129.78:8080/api/device/upload \
  -H "Content-Type: application/json" \
  -d '{"elder_id":"elder_001","heart_rate":125,"spo2":88,"temperature":38.5}'
# 期望: saved=true, need_alarm=true, source=python_ai_service

# 5. 告警列表
curl "http://120.27.129.78:8080/api/alarms?page=1&pageSize=5"
# 期望: 返回告警列表

# 6. 告警转工单（替换为实际 alarm_id）
curl -X POST "http://120.27.129.78:8080/api/alarms/{alarm_id}/to-work-order" \
  -H "Content-Type: application/json" \
  -d '{"handler":"staff_001","handler_name":"社区工作人员"}'
# 期望: work_order_id 非空

# 7. 工单列表
curl "http://120.27.129.78:8080/api/work-orders?page=1&pageSize=5"
# 期望: 返回工单列表

# 8. AI 分析历史（证明数据已入库）
curl "http://120.27.129.78:8080/api/ai/latest-analysis/elder_001"
# 期望: 返回最近一次 AI 分析记录

curl "http://120.27.129.78:8080/api/ai/analysis-records?elder_id=elder_001&page=1&pageSize=10"
# 期望: 返回分页列表，total > 0
```

---

## 7. 常见故障处理

### 7.1 Java 后端挂了

**现象：** `curl :8080/api/health` 无响应或连接拒绝

**排查：**
```bash
sudo systemctl status anxinban-backend
sudo journalctl -u anxinban-backend -n 50
```

**处理：**
```bash
sudo systemctl restart anxinban-backend
```

**如果重启失败：**
1. 检查端口占用：`sudo ss -tlnp | grep 8080`
2. 检查内存：`free -h`（Java 需要约 300MB）
3. 检查 JAR 是否存在：`ls -la /var/www/anxinban-backend/target/*.jar`

### 7.2 Python AI 挂了

**现象：** `/api/ai/service-status` 显示 `current_mode=java_fallback`

**排查：**
```bash
sudo systemctl status yizhi-ai
sudo journalctl -u yizhi-ai -n 50
```

**处理：**
```bash
sudo systemctl restart yizhi-ai
```

**验证恢复：**
```bash
curl http://127.0.0.1:8000/api/health
curl http://120.27.129.78:8080/api/ai/service-status
```

### 7.3 Java 仍走 fallback（Python AI 正常但转发失败）

**排查：**
1. 确认环境变量：`sudo cat /proc/$(pgrep -f anxinban)/environ | tr '\0' '\n' | grep AI_SERVICE`
2. 确认 Java 能连通 Python AI：`curl http://127.0.0.1:8000/api/health`
3. 检查 Java 日志：`sudo journalctl -u anxinban-backend | grep python_ai_service`

**处理：**
- 如果环境变量缺失，重启 Java：
  ```bash
  sudo systemctl restart anxinban-backend
  ```
- 如果需要强制走 fallback（演示降级方案）：
  在 `application.properties` 中设置 `ai.service.enable-forward=false`，然后重启

### 7.4 DeepSeek API 不可用

**现象：** Python AI 日志中出现 API 调用超时或错误

**排查：**
```bash
sudo journalctl -u yizhi-ai | grep -i "deepseek\|error\|timeout"
```

**处理：**
- 短期：Java 后端会自动回退到 `java_rule_fallback`，核心规则引擎仍可生成告警和工单
- 长期：检查 DeepSeek API Key 是否有效，确认 `.env` 文件中 API Key 配置正确

### 7.5 MySQL 连接失败

**现象：** `/api/health` 返回 `database: disconnected` 或 Java 日志中出现 `CommunicationsException`

**排查：**
```bash
sudo systemctl status mysql
sudo ss -tlnp | grep 3306
```

**处理：**
```bash
sudo systemctl restart mysql
sudo systemctl restart anxinban-backend
```

### 7.6 8080 端口占用

**排查：**
```bash
sudo ss -tlnp | grep 8080
sudo lsof -i :8080
```

**处理：**
```bash
# 如果是旧 Java 进程
sudo kill -9 <PID>
sudo systemctl restart anxinban-backend
```

### 7.7 8000 端口占用

**排查：**
```bash
sudo ss -tlnp | grep 8000
```

**处理：**
```bash
sudo systemctl restart yizhi-ai
```

---

## 8. 日志位置

| 服务 | 日志方式 |
|------|---------|
| Java 后端 | `sudo journalctl -u anxinban-backend` |
| Python AI | `sudo journalctl -u yizhi-ai` |
| MySQL | `/var/log/mysql/error.log` |

---

## 9. 备份建议

```bash
# 数据库备份
mysqldump -uroot -p<密码> anxinban > /var/backups/anxinban_$(date +%Y%m%d).sql

# 配置文件备份
cp /etc/systemd/system/anxinban-backend.service /var/backups/
cp /etc/systemd/system/yizhi-ai.service /var/backups/
cp /var/www/anxinban-backend/src/main/resources/application.properties /var/backups/
```
