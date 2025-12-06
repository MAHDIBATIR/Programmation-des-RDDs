@echo off
REM Batch script to run Spark exercises with proper JVM arguments
REM Usage: run-exercice.bat Exercice1Part1
REM        run-exercice.bat Exercice1Part2
REM        run-exercice.bat Exercice2LogAnalysis

if "%1"=="" (
    echo Error: Please specify an exercise name
    echo.
    echo Usage: run-exercice.bat ExerciceName
    echo.
    echo Examples:
    echo   run-exercice.bat Exercice1Part1
    echo   run-exercice.bat Exercice1Part2
    echo   run-exercice.bat Exercice2LogAnalysis
    exit /b 1
)

echo ========================================
echo   Running: %1
echo ========================================
echo.

REM Set environment variable for Maven
set MAVEN_OPTS=-Djava.security.manager=allow

echo Compiling project...
call mvn compile -q

if errorlevel 1 (
    echo Compilation failed!
    exit /b 1
)

echo Compilation successful!
echo.
echo Running %1...
echo.

REM Run the specified exercise
call mvn exec:java -Dexec.mainClass=ma.enset.%1 -q

