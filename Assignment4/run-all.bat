@echo off
cd /d "%~dp0"
call compile.bat
if errorlevel 1 exit /b 1
if not exist evidence mkdir evidence
for %%C in (AtmDemo StorageDemo PortalDemo SalesDemo ScannerDemo SeatBooking BookingExperiment TokenCounter DeadlockDemo TokenExperiment ResultProcessor VariantsRunner) do (
 echo Running %%C ...
 java -cp build %%C > evidence\%%C.txt 2>&1
 if errorlevel 1 (type evidence\%%C.txt & exit /b 1)
)
java -cp build ScannerDemo daemon > evidence\daemon.txt 2>&1
java -cp build UncaughtDemo > evidence\uncaught-stack.txt 2>&1
javac -d build Q1\invalid_compile\BroadCatch.java > evidence\invalid-catch.txt 2>&1
rem The previous two commands are expected to fail; their errors are evidence.
echo Completed. Read evidence logs and capture your own screenshots.
