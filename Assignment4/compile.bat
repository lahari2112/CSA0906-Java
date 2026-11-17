@echo off
cd /d "%~dp0"
if not exist build mkdir build
javac -encoding UTF-8 -g -d build Q1\*.java Q2\*.java Q3\*.java Q4\*.java Q5\*.java Q6\*.java Q7\*.java
if errorlevel 1 exit /b 1
if not exist build\unsafe mkdir build\unsafe
javac -g -d build\unsafe Q5\unsafe\SeatBooking.java
if errorlevel 1 exit /b 1
if not exist build\if_variant mkdir build\if_variant
javac -g -d build\if_variant Q6\if_variant\TokenCounter.java
if errorlevel 1 exit /b 1
echo Compilation successful. Deliberately invalid BroadCatch.java is excluded.
