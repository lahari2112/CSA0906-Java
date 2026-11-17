# Q7 - Concurrent fault-tolerant semester result processing
## Stage 1 - Joint requirements
One file contains three comma-separated integer marks in 0..100. A valid file is successfully processed even if its academic grade is FAIL. A PASS requires every mark >=40; average is the arithmetic mean. These explicit project rules make the summary hand-checkable. File identity is its unique filename in the manifest; duplicates are rejected before scheduling.
Measurable requirements: process each of 10,000 manifest entries once; emit exactly one outcome per entry; classify all expected corrupt/missing files without stopping other tasks; verify successes + failures = manifest size; report timing for pools 1, 2, 4 and 8; retain error reasons; close per-file resources. The exercise specifies no fixed deadline, so speed is measured comparatively rather than inventing an SLA.
Reading/validating separate files and calculating per-student grades are parallel tasks. The input manifest is read-only. Results are shared through ConcurrentHashMap keyed by unique filename; individual task variables are local. Expected failures include missing/unreadable files, malformed integers, wrong field count and out-of-range marks. Other runtime failures appear through ExecutionException and become explicit failure outcomes. Fatal JVM Errors and main-thread interruption abort the run rather than claiming a complete successful summary.
## Stage 2 - Student A design
[DIAGRAM:Q7]
ScoreTask implements Callable<Outcome>. A fixed ExecutorService runs a bounded number of worker threads. Each wrapper publishes its task's Outcome into ConcurrentHashMap and returns it through a Future. Main waits on every Future; ordinary unexpected task exceptions produce a failure outcome under that task ID. The map avoids concurrent writes corrupting shared state and provides one slot per unique manifest entry. A nonconcurrent list collected only by main would also be valid, but would not demonstrate the selected shared concurrent collection.
```text
ALGORITHM PROCESS_RESULTS(Manifest, PoolSize)
BEGIN
    REJECT duplicate manifest IDs
    CREATE fixed executor and concurrent outcome map
    FOR EACH file in Manifest DO
        SUBMIT callable: read, validate, compute, publish one Outcome
        SAVE returned Future associated with that file
    ENDFOR
    FOR EACH saved Future DO
        TRY wait for completion
        ON expected/unexpected ordinary failure record failure for that ID
        ON interruption cancel pending tasks and report incomplete run
        ON fatal VM Error abort instead of promising recovery
    ENDFOR
    REQUIRE outcome count equals manifest count
    SORT outcomes by ID; COUNT success and failure
    SHUT DOWN executor; RETURN outcomes
END
```
Known I/O/validation failures are converted inside call; Future.get catches exceptions escaping that boundary. Completion of every Future is the synchronization point before summarization. The implementation shuts down in finally. At most p score reads are active, but Files.readString loads each entire file, so impose a file-size limit in production.
## Stage 3 - Student B implementation and evidence
| Small input | Content | Expected and observed |
|---|---|---|
| a.txt | 60,70,80 | Processed; average 70; PASS |
| b.txt | 30,40,50 | Processed; average 40; FAIL |
| c.txt | 40,40,40 | Processed; average 40; PASS |
| d.txt | 100,100,100 | Processed; average 100; PASS |
| corrupt.txt | oops | Processing failure |
| missing.txt | Intentionally absent | Processing failure |
The generator makes a 10,000-entry manifest; indices divisible by 100 are intentionally absent (100 files), and indices with remainder 1 contain CORRUPT (100 files). The other 9,800 contain 60,70,80. All 12 measured runs reported 9,800 successful processing outcomes and 200 failures. Assertions also verified 100 missing files, valid averages/grades and the small hand-checked results. No unhandled ordinary per-file failure was observed. Raw CSV and a complete 10,000-row summary are included; generated temporary input files are cleaned up after execution and can be reproduced by the generator.
Timing includes executor startup, task submission, reading, parsing, Future waiting, result-map processing, sorting and shutdown. Data generation and summary-file writing are outside the timing. Three trials rotate pool order; a short warmup occurs first. Inputs were just generated, so page-cache effects are substantial. These are host observations, not guarantees for a student's disk or a production server.
## Stage 4 - Joint defense and limitations
Recommend the pool with the best repeatable median for this workload, then repeat on the target machine with representative file sizes and storage. More threads do not guarantee improvement: I/O bandwidth, CPU parsing, task overhead, context switching and concurrent-map contention can dominate. The first trial already shows four threads slower than two, while later samples differ. Do not infer a universal winner from one run.
A complete normal-run summary follows from unique IDs, one task per entry, a returned or explicitly failed outcome for every Future, waiting for all tasks and asserting the map size. It does not guarantee completion after process crash, fatal error, indefinitely blocked I/O or forced cancellation. Remaining limitations: all Futures/outcomes and the unbounded executor work queue cost O(n); whole-file reads are unbounded; results are not checkpointed for recovery. For larger production workloads use a bounded submission window, size limits, deadlines and durable checkpoints.
## Alternative designs and complexity
parallelStream can map each path through the same local failure-to-Outcome function and collect the results. Without local handling one unchecked failure can abort the terminal operation. Its default shared common pool is less explicit for blocking I/O and competes with unrelated users of that pool.
One platform Thread per file can run the same guarded task, but 10,000 stacks require O(n*S) memory and heavy scheduling overhead. The fixed pool uses O(p*S) stack memory plus O(n) tasks/Futures/results and O(p*L) active file content for maximum file size L. Total input-processing work is O(B+n) for B bytes; ideal parallel time approaches O(B/p) subject to I/O and scheduling limits. This implementation additionally sorts results in O(n log n), which is included in timing and must not be omitted from its total complexity.

## Measured pool-size comparison
Three trials per pool; milliseconds, on OpenJDK 21.0.13 / Linux.

| Pool | Trial 1 | Trial 2 | Trial 3 | Median ms |
|---|---|---|---|---|
| 1 | 500.892 | 157.099 | 179.238 | 179.238 |
| 2 | 137.488 | 72.525 | 98.800 | 98.800 |
| 4 | 145.872 | 44.223 | 63.164 | 63.164 |
| 8 | 75.134 | 52.164 | 44.611 | 52.164 |

The observed best median was pool 8 (52.164 ms). Use 8 as the starting recommendation for this measured local workload, then validate on the deployment machine.

[CHART:q7]
