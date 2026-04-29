@echo off
cd /d "%~dp0"
echo Compiling EmployeeApp...
javac -cp "src\main\resources\*" -d "src\main\java" "src\main\java\EmployeeApp.java"
if %errorlevel% neq 0 (
    echo Compilation FAILED!
    pause
    exit /b 1
)

echo Running EmployeeApp...
java -cp "src\main\java;src\main\resources\*" EmployeeApp
pause

