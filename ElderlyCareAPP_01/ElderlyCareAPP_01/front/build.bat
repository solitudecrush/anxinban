@echo off
set JAVA_HOME=E:\src\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
echo JAVA_HOME=%JAVA_HOME%
echo Building Flutter APK...
flutter build apk --release
pause
