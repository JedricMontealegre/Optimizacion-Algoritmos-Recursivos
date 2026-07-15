@echo off
setlocal
cd /d "%~dp0\.."
if exist out rmdir /s /q out
mkdir out\main
mkdir out\test
if exist main_sources.txt del main_sources.txt
if exist test_sources.txt del test_sources.txt
for /r src\main\java %%f in (*.java) do echo %%f>>main_sources.txt
javac -encoding UTF-8 -d out\main @main_sources.txt
if errorlevel 1 exit /b 1
for /r src\test\java %%f in (*.java) do echo %%f>>test_sources.txt
javac -encoding UTF-8 -cp out\main -d out\test @test_sources.txt
if errorlevel 1 exit /b 1
del main_sources.txt test_sources.txt
echo Compilacion finalizada.
