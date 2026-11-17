# CSA09 - Assignment 3 - Exceptions and Multithreading
Student A name/register: **FILL IN**
Student B name/register: **FILL IN**
Student A GitHub URL: **FILL IN**
Student B GitHub URL: **FILL IN**
Your Java version: **paste java -version output**
Preparation validation: OpenJDK 21.0.13, Linux. Minimum project target: JDK 17.

This is pair work, but BOTH students must execute EVERY question in their OWN repositories. The included observations are real preparation-host runs, not claims of either student's participation. Fill the contribution record based on actual work. No MySQL or third-party libraries are required.

## Run on Windows
Extract the ZIP. Open Command Prompt in the Assignment3 folder. Run:
```
compile.bat
run-all.bat
```
The normal build intentionally excludes Q1/invalid_compile/BroadCatch.java. Unsafe SeatBooking and the if-variant TokenCounter use separate build directories. run-all logs intentional failures separately; it does not treat them as successful examples.

## Programs and observed results
| Question | Command after compiling | Observed preparation result |
|---|---|---|
| Q1 | java -cp build AtmDemo | Seven expected paths verified; finally ran |
| Q1 interactive | java -cp build AtmDemo interactive | Enter fake account 1001 and amount |
| Q2 | java -cp build StorageDemo | Matches all three expected lines |
| Q2 portal | java -cp build PortalDemo | Five scenarios; original cause retained |
| Q3 | java -cp build SalesDemo | Identical valid reports; leak counts 1/0/0 |
| Q4 | java -cp build ScannerDemo | 30 recorded orders; totals all 40 |
| Q4 daemon | java -cp build ScannerDemo daemon | Began; did not finish before JVM exit |
| Q5 | java -cp build SeatBooking | Seats sold: 10 |
| Q5 experiments | java -cp build BookingExperiment | Forced race reproduced; locked versions correct |
| Q6 | java -cp build TokenCounter | Sum 210 |
| Q6 deadlock | java -cp build DeadlockDemo | Two deadlocked threads detected; ordered transfers finish |
| Q6 counter | java -cp build TokenExperiment | Correct counter sum 3240; invalid variants investigated |
| Q6 variants | java -cp build VariantsRunner | Ten unsafe booking and ten if-counter processes, bounded by timeouts |
| Q7 | java -cp build ResultProcessor | Every pool: 9800 processed, 200 input failures |

Additional deliberate errors:
```
java -cp build UncaughtDemo
javac -d build Q1\invalid_compile\BroadCatch.java
```
Both should fail; preserve their diagnostics as evidence. See STEP_BY_STEP.md for debugger commands. All file programs must run from the project root so relative input paths work.

Linux/macOS compilation:
```
mkdir -p build build/unsafe build/if_variant
javac -encoding UTF-8 -g -d build Q1/*.java Q2/*.java Q3/*.java Q4/*.java Q5/*.java Q6/*.java Q7/*.java
javac -g -d build/unsafe Q5/unsafe/SeatBooking.java
javac -g -d build/if_variant Q6/if_variant/TokenCounter.java
```
Run individual Java commands from the table. No classpath libraries are needed.

## Evidence and report
Central evidence contains actual logs, CSVs, stack traces and a jdb transcript. Copies are placed in each question's evidence folder. Q7 regenerates 10,000 inputs in a temporary folder and deletes them after measurement, keeping the manifest rule and full output summary. Screenshots must come from each student's own execution; capture commands and meaningful output, not only a final message. Edit Assignment3_Report.md or the printable editable HTML, add names, URLs, your own measured results and screenshots, then export the final PDF.

## GitHub
Name your repository CSA09-YOURREGISTERNUMBER-Assignment3. Commit each question progressively with its input/evidence/screenshots. Upload extracted source folders, not just the ZIP. Exclude build and class files. Make faculty access work. Student B must use their own register number and repository. Do not backdate or invent a contribution history.
