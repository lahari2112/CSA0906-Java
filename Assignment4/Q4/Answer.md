# Q4 - Thread creation and the thread life cycle
## Shared implementation
ScannerDemo implements four stations extending Thread, then four Runnable tasks wrapped in Thread objects. Every station scans ten scripts with a simulated 1 ms sleep per script. All workers start before main joins them; the expected final total is 40. Actual totals are asserted for every run. Ten normal Thread runs, ten priority experiments and ten Runnable runs are recorded in evidence/q4-orders.csv.
## Student A - Design and states
Thread inheritance combines task and thread identity and prevents extending another class. Runnable separates work from scheduling, supports composition and can be submitted to an ExecutorService; it is usually preferable for reusable tasks. The included fixed-pool example submits four Callable tasks and sums their returned counts through Future.get.
[DIAGRAM:Q4]
NEW precedes start. RUNNABLE includes running or ready-to-run at JVM level. sleep places a worker in TIMED_WAITING; a contended monitor can place it in BLOCKED. wait or untimed join can place the calling thread in WAITING; notification does not transfer lock ownership immediately. Completion leads to TERMINATED. Not every worker visits every state. In this scanning design workers alternate RUNNABLE and TIMED_WAITING; main may WAIT while joining. A transient state snapshot is not guaranteed to catch every state.
Calling direct.run() executes on the current main thread. The observed log says "run() executes on main" and the never-started Thread remains NEW with isAlive=false. start() creates concurrent execution and can be called only once per Thread. Successful join waits for termination and makes the worker's prior writes visible, so main reads each scanned counter safely after joining. It does not dictate completion order.
## Student B - Scheduler, priorities and daemon evidence
The full 30-order CSV is the evidence, including totals and the priority flag. Normal and priority runs can have different completion orders; priorities are scheduler hints, not a portable ordering contract. Station 1 is assigned MAX_PRIORITY and the other stations MIN_PRIORITY in the priority trials. No result computation depends on which finishes first.
The daemon experiment runs in a separate JVM. A latch confirms "daemon began" before main prints "main ends without joining daemon". The observed output has no "daemon completed": the JVM can exit when only daemon threads remain. Joining a daemon would change that behavior by keeping main alive until it finishes. Essential result or payment work must not rely on daemon completion.
## Joint conclusion and pool alternative
Scheduling, CPU load, OS policy, timer granularity and lock acquisition cause nondeterminism. A correct program waits for all required results and uses synchronization for shared state rather than relying on observed order. The synchronized finish-order list is shared; individual scan counts belong to one worker and are read after join.
For n balanced scripts taking t each and p workers, ideal processing time is O(n*t/p), plus startup, scheduling and aggregation; fixed overhead and I/O can dominate. Four Thread instances and a four-worker pool both use roughly four thread stacks, O(p*S) for stack size S. A pool reuses workers across repeated batches and prevents unbounded thread creation; its queue and Futures add O(number of tasks) memory. More workers than available useful parallelism may increase context switching instead of throughput.

## Recorded station completion order
Each row completes 40 scripts. A four-digit order lists station IDs in completion order.

| Trial | Thread normal | Thread priorities | Runnable |
|---|---|---|---|
| 1 | 1-2-4-3 | 2-1-4-3 | 1-2-4-3 |
| 2 | 3-1-2-4 | 1-4-3-2 | 1-2-4-3 |
| 3 | 1-2-4-3 | 2-1-3-4 | 1-3-2-4 |
| 4 | 2-1-4-3 | 3-2-1-4 | 2-3-4-1 |
| 5 | 1-3-2-4 | 1-2-3-4 | 1-2-3-4 |
| 6 | 1-2-3-4 | 1-2-3-4 | 1-3-4-2 |
| 7 | 1-2-3-4 | 1-3-2-4 | 1-2-3-4 |
| 8 | 1-2-3-4 | 1-2-3-4 | 1-2-4-3 |
| 9 | 1-2-3-4 | 1-2-3-4 | 1-2-3-4 |
| 10 | 1-2-3-4 | 2-1-3-4 | 1-2-3-4 |
