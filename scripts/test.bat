@echo off
setlocal
cd /d "%~dp0\.."
call scripts\compile.bat
if errorlevel 1 exit /b 1
java -ea -cp "out\main;out\test" com.proyecto.recursivos.AlgorithmTests
