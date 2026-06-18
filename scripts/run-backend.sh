#!/usr/bin/env bash
# 安心伴后端启动脚本（Linux/macOS）
# 本脚本自动检测并设置 JAVA_HOME，然后使用 Maven Wrapper 启动 Spring Boot 后端。
# 如需要指定端口，可传入参数：./run-backend.sh --server.port=8081

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

# 1. 检查当前 JAVA_HOME 是否可用
if [[ -n "${JAVA_HOME:-}" && -x "${JAVA_HOME}/bin/javac" ]]; then
    echo "[INFO] 使用已配置的 JAVA_HOME: ${JAVA_HOME}"
else
    echo "[WARN] JAVA_HOME 未设置或不可用，尝试自动查找 JDK 17..."

    # 2. 自动搜索常见 JDK 安装路径
    CANDIDATES=(
        "/usr/lib/jvm/java-17-openjdk"
        "/usr/lib/jvm/java-17-openjdk-amd64"
        "/usr/lib/jvm/java-17-oracle"
        "/usr/lib/jvm/jdk-17"
        "/opt/jdk-17"
        "/e/src/Java/jdk-17"
        "/d/Program Files/Java/jdk-17"
        "$HOME/.jdks/corretto-17"
        "$HOME/.jdks/temurin-17"
    )

    FOUND=""
    for CANDIDATE in "${CANDIDATES[@]}"; do
        if [[ -x "${CANDIDATE}/bin/javac" ]]; then
            FOUND="${CANDIDATE}"
            break
        fi
    done

    # 3. 尝试使用 /usr/libexec/java_home（macOS）
    if [[ -z "${FOUND}" && -x "/usr/libexec/java_home" ]]; then
        MAC_JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null || true)
        if [[ -n "${MAC_JAVA_HOME}" && -x "${MAC_JAVA_HOME}/bin/javac" ]]; then
            FOUND="${MAC_JAVA_HOME}"
        fi
    fi

    # 4. 仍未找到，提示用户
    if [[ -z "${FOUND}" ]]; then
        echo "[ERROR] 无法找到可用的 JDK 17 安装路径。"
        echo "[ERROR] 请安装 JDK 17 并设置环境变量 JAVA_HOME，或在脚本中补充候选路径。"
        exit 1
    fi

    export JAVA_HOME="${FOUND}"
    export PATH="${JAVA_HOME}/bin:${PATH}"
    echo "[INFO] 自动设置 JAVA_HOME: ${JAVA_HOME}"
fi

if ! command -v java >/dev/null 2>&1; then
    echo "[ERROR] 无法执行 java 命令，请检查 JAVA_HOME 配置。"
    exit 1
fi

echo "[INFO] 正在启动安心伴后端..."
cd "${PROJECT_DIR}"

# 将脚本接收到的参数透传给 Spring Boot 应用（例如 --server.port=8081）
if [[ $# -gt 0 ]]; then
    ./mvnw spring-boot:run -Dspring-boot.run.arguments="$*"
else
    ./mvnw spring-boot:run
fi
