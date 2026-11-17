# Assignment 3 - Windows step-by-step guide
## Step 1 - Download and extract
Download Assignment3_Project_Files.zip. Right-click > Extract All. Open the extracted Assignment3 folder. Use a simple local folder such as C:\Assignment3. Do not run inside the ZIP.
## Step 2 - Fill student details
Edit README.md, CONTRIBUTIONS.md and Assignment3_Report.md. Enter both students' names/register numbers and later add both repository links. Both students must do every question and keep their own repository.
## Step 3 - Check Java
Open Command Prompt and run java -version and javac -version. Use JDK 17 or newer. If javac is not recognized, install a JDK, add its bin folder to PATH, and reopen Command Prompt. This project needs no MySQL or external JAR files.
## Step 4 - Open the correct folder in Command Prompt
In File Explorer open Assignment3, click its address bar, type cmd and press Enter. Commands now run beside the Q1..Q7 folders, so input paths resolve correctly.
## Step 5 - Compile
Run compile.bat. You should see Compilation successful. The intentionally invalid catch-order example is excluded. The unsafe SeatBooking and if-variant TokenCounter have the same class names as their corrected versions, so the script compiles them into separate directories; do not compile every subfolder into one directory.
## Step 6 - Run and inspect Q1
Run java -cp build AtmDemo. Verify seven cases and finally messages. Then run java -cp build AtmDemo interactive and enter 1001 followed by 500.
Run java -cp build UncaughtDemo to show the intentional exception trace. Run javac -d build Q1\invalid_compile\BroadCatch.java to show the intentional compiler error. These two failures are expected evidence, not installation problems. Screenshot the commands and complete error messages.
## Step 7 - Run Q2
Run java -cp build StorageDemo and java -cp build PortalDemo. Capture the expected three scaffold lines, portal failure scenarios, TYPE/CAUSE output and the chained stack trace. Explain why omitting the cause produces null and why calling getMessage on that null causes another exception.
## Step 8 - Run Q3 and debug the leak
Run java -cp build SalesDemo. Open Q3\generated\manual.csv and twr.csv; they should match. The leak counts should be buggy=1, manual=0 and twr=0. The corrupt file is Q3\input\corrupt.txt, line 2.
For the required debugger evidence, run jdb -classpath build -sourcepath Q3 SalesDemo. At the debugger prompt issue each command below and wait for the next prompt:
```
stop in SalesDemo.buggy
run
next
next
print SalesDemo.live.size()
catch java.io.IOException
cont
where
print SalesDemo.live.size()
locals
ignore java.io.IOException
cont
```
The first resource count should be 1 and the exception-time count 2. After continuing, the program logs that the buggy version leaves 1 resource open. Capture breakpoint, locals and stack views. The package includes a real preparation debugger transcript for comparison. If jdb is missing, install the full JDK; an IDE debugger can also set a method breakpoint in buggy and an exception breakpoint for IOException.
## Step 9 - Run Q4
Run java -cp build ScannerDemo. It records ten normal Thread trials, ten priority trials and ten Runnable trials in evidence\q4-orders.csv. Totals must be 40; order may differ from the report. Run java -cp build ScannerDemo daemon in a separate command to observe JVM exit. Capture NEW/alive state, post-join state, direct run() output and daemon output.
## Step 10 - Run Q5
Run java -cp build SeatBooking and java -cp build BookingExperiment. Corrected scaffold output is Seats sold: 10. The forced-race experiment must accept two bookings for one seat, while locked benchmark versions accept 100 seats out of 10,000 attempts. Open evidence\q5-bookings.csv for ten trials per strategy. Report your actual unsynchronized outcome even if it happens to be correct.
## Step 11 - Run Q6
Run java -cp build TokenCounter, java -cp build DeadlockDemo and java -cp build TokenExperiment. Corrected scaffold sum is 210. DeadlockDemo deliberately detects blocked daemon threads then demonstrates ordered transfers; it exits normally, so do not remove its daemon safeguard. TokenExperiment captures size history and compares while/if and notify/notifyAll; incorrect experiments have bounded waits and can print ERROR or TIMEOUT as evidence.
Run java -cp build VariantsRunner to execute ten original unsafe SeatBooking and ten if-counter scaffold runs. Each subprocess is stopped after three seconds if needed. Read evidence\unsafe-scaffold.txt and evidence\if-scaffold.txt. Do not conclude a broken method is correct merely because these small runs succeed.
## Step 12 - Run Q7
Run java -cp build ResultProcessor. It generates 10,000 manifest entries with 100 missing and 100 corrupt inputs, runs pools 1/2/4/8 three times, and saves timing and result CSVs. Every large run should report 9800 successful processing outcomes and 200 failures. Four valid files plus two failures must appear in the small test. A valid file with a failing academic grade is still a processing success.
Open evidence\q7-timings.csv and q7-small-results.csv. Use the median of three timings for each pool. Your fastest pool may differ from the preparation host; explain your observed result. No temporary 10,000-file dataset needs to be uploaded because the Java generator reproduces it.
## Step 13 - Optional complete rerun
run-all.bat compiles and executes all main programs, intentional error demonstrations and timeout-protected variants. It writes logs under evidence. It does not conduct your interactive debugger session; complete Step 8 yourself. Programs may take a minute or longer depending on the computer. Do not close the window while Q7 or the variants run. If the script stops early, read the last program's log.
## Step 14 - Save screenshots and update the report
Use Win+Shift+S. Store images in Q1\screenshots through Q7\screenshots. Show commands, inputs, trace details, timing tables, sizes and summary counts, not just "complete". Copy relevant central evidence files into each question's evidence folder after your rerun so the task-local evidence is current. Keep raw CSVs.
Edit Assignment3_Report.md using a Markdown editor, or open Assignment3_Report.html in a browser. The HTML is editable: click text, replace details/results, then Ctrl+P > Save as PDF. Browser text edits last only until refresh unless explicitly saved; export before closing. For adding screenshots, copying the editable report into Word and inserting images may be easier. The ZIP includes diagrams and charts. Regenerate/replace charts with your own measurements if they differ; label the original host measurements if retained. Fill CONTRIBUTIONS.md with genuine actions and commit links.
## Step 15 - Create GitHub repositories
Each student signs in to GitHub and creates CSA09-THEIROWNREGISTERNUMBER-Assignment3. Use public, or private with faculty access. The report needs both URLs. Do not use your partner's register number for your own repository.
## Step 16 - Upload progressively
After completing Q1, upload README, compile/run scripts, .gitignore and Q1 with input, answers and evidence; commit with a descriptive Q1 message. Then add Q2 after doing it, followed by Q3 through Q7, each with its own genuine commit. Use Add file > Upload files; for an empty repository click uploading an existing file. Upload extracted folders, not just the ZIP. Exclude build and class files. Add report diagrams/charts when the report is uploaded so its relative image links work. Do not create backdated history or claim unperformed pair contributions.
## Step 17 - Submit
Check that all Java files open on GitHub and screenshots are visible. Enter both repository links on the cover, export the updated final report PDF and submit through your faculty's required portal. For private repositories invite the faculty's correct GitHub account. Keep your local project so you can demonstrate every question during verification.
## Troubleshooting
Cannot find main class: run compile.bat and stay in the project root.
NoSuchFileException for valid inputs: open Command Prompt in Assignment3, not in Q1 or Q7.
Duplicate class error: compile using the provided script; unsafe variants have separate output folders.
Different thread order/timing: expected; record actual values.
An unsafe variant times out: expected possible observation; the runner stops only that child process.
Compiler error in BroadCatch: intentional; it demonstrates unreachable catch order.
A required correct program reports AssertionError: preserve the log and investigate before submitting; do not remove the assertion to hide the issue.
