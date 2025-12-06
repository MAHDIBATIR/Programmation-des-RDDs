# PowerShell script to run Spark exercises with proper JVM arguments
# Usage: .\run-exercice.ps1 Exercice1Part1
#        .\run-exercice.ps1 Exercice1Part2
#        .\run-exercice.ps1 Exercice2LogAnalysis

param(
    [Parameter(Mandatory=$true)]
    [string]$ExerciceName
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Running: $ExerciceName" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Set environment variable for Maven
$env:MAVEN_OPTS = "-Djava.security.manager=allow"

# Compile if needed
Write-Host "Compiling project..." -ForegroundColor Yellow
mvn compile -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Running $ExerciceName..." -ForegroundColor Yellow
    Write-Host ""

    # Run the specified exercise
    mvn exec:java "-Dexec.mainClass=ma.enset.$ExerciceName" -q
} else {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit 1
}

