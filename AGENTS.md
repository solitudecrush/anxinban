# AGENTS.md — 安心伴后端开发指南

## 项目定位

`anxinban/` 是安心伴智慧家居/养老守护系统的核心 Spring Boot 后端，
负责 REST API、MQTT 设备接入、数据持久化、AI 智能体联动等业务。

## 技术栈

- **语言/框架**：Java 17 + Spring Boot 4.0.6 + Spring Data JPA
- **数据库**：MySQL 8.0.33
- **消息队列**：MQTT（Eclipse Paho 客户端，对接 EMQX Broker）
- **JSON**：Jackson 2.x（已排除 Spring Boot 4.x 默认的 Jackson 3.x）
- **构建工具**：Maven

## 代码组织

```
src/main/java/com/anxinban/
├── AnxinbanApplication.java      # 启动类
├── config/                       # 全局配置（CORS、Jackson 等）
├── controller/                   # REST 控制器，仅做参数接收与响应封装
├── service/                      # 业务逻辑层
├── entity/                       # JPA 实体
├── mapper/                       # JPA Repository（按 Spring Data 命名习惯称 mapper）
├── dto/                          # 请求/响应 DTO
└── mqtt/                         # 物联网模块
    ├── config/                   # MQTT 配置属性
    ├── constant/                 # 主题常量
    ├── dto/                      # 设备消息 DTO
    ├── service/                  # MQTT 客户端、规则引擎、AI 服务
    └── simulator/                # 设备模拟器
```

## 注释规范

- 所有公共类、接口、方法必须包含 Javadoc。
- 使用中文注释，便于团队阅读。
- 类级 Javadoc 说明职责、关联模块、作者及版本。
- 方法级 Javadoc 说明业务含义、参数、返回值及异常。
- 复杂逻辑、边界条件、临时方案需加行内注释。

## 开发约定

- **统一响应**：Controller 统一返回 `ApiResponse<T>`，成功 `ApiResponse.success(...)`，
  失败 `ApiResponse.error(code, message)`。
- **数据访问**：每个实体对应一个 `*Repository` 接口，放在 `mapper` 包下。
- **事务**：Service 层涉及多表写入的方法应加 `@Transactional`。
- **配置**：新增配置属性优先使用 `@ConfigurationProperties`，并在 `application.properties`
  中补充分区说明。

## 构建与验证

```bash
# 编译
cd anxinban
mvn compile

# 运行（推荐：自动检测 JDK）
cd anxinban
# Windows
scripts\\run-backend.bat
# Linux / macOS
chmod +x scripts/run-backend.sh
./scripts/run-backend.sh

# 手动运行（需确保 JAVA_HOME 指向 JDK 17）
cd anxinban
mvn spring-boot:run
```

## 常见问题

1. **`JAVA_HOME is not defined correctly` / 找不到 JDK**：
   环境变量 `JAVA_HOME` 可能指向了不存在的路径（如 `D:\Program Files\Java\jdk-17`）。
   请使用 `scripts/run-backend.bat`（Windows）或 `scripts/run-backend.sh`（Linux/macOS）
   自动检测 JDK；或手动将 `JAVA_HOME` 设置为实际安装的 JDK 17 目录。
2. **Jackson 版本**：Spring Boot 4.0.6 默认引入 Jackson 3.x（`tools.jackson`），
   本项目统一使用 Jackson 2.x（`com.fasterxml.jackson`）。
   已在 `pom.xml` 中排除 `spring-boot-starter-jackson`，并通过 `config/JacksonConfig.java`
   显式暴露 Jackson 2.x 的 `ObjectMapper` Bean。
3. **MySQL 数据源**：Spring Boot 4.0.6 在特定依赖组合下无法自动推断 MySQL 驱动类。
   已在 `config/DataSourceConfig.java` 中显式构造 `HikariDataSource` 并指定驱动类。
4. **MySQL 连接失败**：检查 `application.properties` 中数据库地址、用户名、密码；
   确认 MySQL 服务已启动并创建 `anxinban` 数据库。
5. **MQTT 连接失败**：确认 EMQX Broker 已启动，且配置项 `mqtt.broker-host` 正确；
   或在测试/开发环境设置 `mqtt.enabled=false`。
6. **端口占用**：默认监听 `8080`，若被占用可在启动时指定端口，例如
   `scripts/run-backend.bat --server.port=8081`。
7. **配置文件**：`application.properties` 避免使用中文注释（Java Properties 默认按 ISO-8859-1 解析）。
   详细配置说明请参见本文件及 `README.md`。

## 辅助脚本

- `scripts/add_comments.py`：批量为 DTO/Entity/Mapper 等补充基础 Javadoc。
- `scripts/cleanup_comments.py`：清理重复或低质量注释。
- `scripts/fix_post_annotation_docs.py`：修复注解后错位的 Javadoc。
- `scripts/add_controller_method_docs.py`：为 Controller 方法补充基于注解的 Javadoc。

> 这些脚本仅用于提升注释覆盖率，复杂业务方法仍需人工完善。
