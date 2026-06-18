@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

:: 安心伴后端启动脚本（Windows）
:: 本脚本自动检测并设置 JAVA_HOME，然后使用 Maven Wrapper 启动 Spring Boot 后端。
:: 如需要指定端口，可传入参数：run-backend.bat --server.port=8081

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."

:: 1. 检查当前 JAVA_HOME 是否可用
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        echo [INFO] 使用已配置的 JAVA_HOME: %JAVA_HOME%
        goto :run
    ) else (
        echo [WARN] JAVA_HOME 指向的目录不存在 javac.exe: %JAVA_HOME%
        echo [WARN] 将尝试自动查找 JDK...
    )
)

:: 2. 自动搜索常见 JDK 安装路径（优先 JDK 17）
set "CANDIDATES="
for %%D in (
    "D:\Program Files\Java\jdk-17"
    "E:\src\Java\jdk-17"
    "C:\Program Files\Java\jdk-17"
    "C:\Program Files\Eclipse Adoptium\jdk-17"
) do (
    if exist "%%~D\bin\javac.exe" (
        set "JAVA_HOME=%%~D"
        goto :found
    )
)

:: 3. 未找到固定路径时，搜索 Program Files 和 E:\src\Java 下的 jdk-* 目录
for /d %%D in ("C:\Program Files\Java\jdk-*") do (
    if exist "%%~D\bin\javac.exe" (
        set "JAVA_HOME=%%~D"
        goto :found
    )
)
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-*") do (
    if exist "%%~D\bin\javac.exe" (
        set "JAVA_HOME=%%~D"
        goto :found
    )
)
for /d %%D in ("E:\src\Java\jdk-*") do (
    if exist "%%~D\bin\javac.exe" (
        set "JAVA_HOME=%%~D"
        goto :found
    )
)

:: 4. 仍未找到，提示用户
if not defined JAVA_HOME (
    echo [ERROR] 无法找到可用的 JDK 17 安装路径。
    echo [ERROR] 请安装 JDK 17 并设置环境变量 JAVA_HOME，或在脚本中补充候选路径。
    pause
    exit /b 1
)

:found
echo [INFO] 自动设置 JAVA_HOME: %JAVA_HOME%
set "PATH=%JAVA_HOME%\bin;%PATH%"

:run
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] 无法执行 java 命令，请检查 JAVA_HOME 配置。
    pause
    exit /b 1
)

echo [INFO] 正在启动安心伴后端...
cd /d "%PROJECT_DIR%"

:: 将脚本接收到的参数透传给 Spring Boot 应用（例如 --server.port=8081）
if "%~1"=="" (
    call "%PROJECT_DIR%\mvnw.cmd" spring-boot:run
) else (
    call "%PROJECT_DIR%\mvnw.cmd" spring-boot:run -Dspring-boot.run.arguments="%*"
)

if errorlevel 1 (
    echo [ERROR] 后端启动失败。
    pause
    exit /b 1
)

endlocal
