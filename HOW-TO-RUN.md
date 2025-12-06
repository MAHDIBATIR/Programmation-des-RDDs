# How to Run Spark Exercises

## Problem: Java 23 Security Manager Error

All Spark exercises require the JVM argument `-Djava.security.manager=allow` because Java 23 removed the Security Manager by default, but Spark still needs it.

---

## Method 1: Run from IntelliJ IDEA (Recommended)

### One-Time Setup:
1. Click the run configuration dropdown (top right, next to green play button)
2. Select **"Edit Configurations..."**
3. For each exercise configuration (Exercice1Part1, Exercice1Part2, etc.):
   - Click **"Modify options"** → Check **"Add VM options"**
   - In the "VM options" field, enter: `-Djava.security.manager=allow`
   - Click **"Apply"**
4. Click **"OK"**

### Run:
Just click the green Run button normally!

---

## Method 2: Run from Command Line (PowerShell)

### Option A: Use the helper script
```powershell
.\run-exercice.ps1 Exercice1Part1
.\run-exercice.ps1 Exercice1Part2
.\run-exercice.ps1 Exercice2LogAnalysis
```

### Option B: Use Maven directly
```powershell
# Set the environment variable
$env:MAVEN_OPTS="-Djava.security.manager=allow"

# Run any exercise
mvn exec:java -Dexec.mainClass=ma.enset.Exercice1Part1
mvn exec:java -Dexec.mainClass=ma.enset.Exercice1Part2
mvn exec:java -Dexec.mainClass=ma.enset.Exercice2LogAnalysis
```

---

## Exercises Overview

### Exercice 1 Part 1: Total Sales by City
Calculates total sales amount for each city.

**Run:**
```powershell
$env:MAVEN_OPTS="-Djava.security.manager=allow"
mvn exec:java -Dexec.mainClass=ma.enset.Exercice1Part1
```

**Expected Output:**
```
========================================
    TOTAL SALES BY CITY
========================================
Casablanca      : 58200.00 DH
Rabat           : 42100.00 DH
Marrakech       : 35000.00 DH
========================================
```

---

### Exercice 1 Part 2: Total Sales by Product and Year
Calculates total sales for each product per year.

**Run:**
```powershell
$env:MAVEN_OPTS="-Djava.security.manager=allow"
mvn exec:java -Dexec.mainClass=ma.enset.Exercice1Part2
```

**Expected Output:**
```
========================================
  TOTAL SALES BY PRODUCT AND YEAR
========================================
2023 - Laptop        : 49000.00 DH
2023 - Phone         : 14000.00 DH
2023 - Tablet        : 15000.00 DH
2024 - Laptop        : 13500.00 DH
2024 - Phone         : 7400.00 DH
2024 - Tablet        : 11400.00 DH
========================================
```

---

### Exercice 2: Log Analysis
Analyzes Apache access logs.

**Run:**
```powershell
$env:MAVEN_OPTS="-Djava.security.manager=allow"
mvn exec:java -Dexec.mainClass=ma.enset.Exercice2LogAnalysis
```

---

## Troubleshooting

### Error: "UnsupportedOperationException: getSubject is supported only if a security manager is allowed"
**Solution:** You forgot to add the `-Djava.security.manager=allow` VM option.

### Error: "ClassNotFoundException"
**Solution:** Compile first with `mvn compile`

### Logs are too verbose
**Solution:** The INFO logs are normal. Look for the section headers (====) to find your actual results.

---

## Quick Reference

| Exercise | Main Class | Purpose |
|----------|-----------|---------|
| Part 1.1 | `ma.enset.Exercice1Part1` | Sales by City |
| Part 1.2 | `ma.enset.Exercice1Part2` | Sales by Product & Year |
| Part 2 | `ma.enset.Exercice2LogAnalysis` | Log Analysis |

---

## Files

- `ventes.txt` - Sales data (date, city, product, price)
- `access.log` - Apache access logs
- `run-exercice.ps1` - Helper script to run exercises easily

