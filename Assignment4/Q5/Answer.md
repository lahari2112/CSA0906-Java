# Q5 - Race conditions in cinema seat booking
## Shared implementation and completed scaffold
SeatBooking.java changes only TODO 1: synchronized book checks booked[seat], sets it true and increments bookingsMade under the same receiver monitor. The corrected scaffold prints Seats sold: 10. The separate unsafe folder contains the same class with only synchronized removed; it is compiled into a different directory so it does not collide with the correct class.
## Student A - Reproduce and explain the race
The original unsafe scaffold returned 10 in all ten preparation runs. This does not prove safety: thread startup often serializes short operations, and the unprotected bookingsMade++ can lose increments, masking duplicate sales. Keep the actual result rather than inventing a failure count.
BookingExperiment adds a separate controlled interleaving: two users both read one seat as free, meet at a CyclicBarrier, then both mark it sold. An independent AtomicInteger counts successful returns. This produced two accepted sales for one seat in all ten trials: one extra sale per trial. The barrier is experimental scheduling instrumentation, not a proposed booking implementation.
| Step | User A | User B |
|---|---|---|
| 1 | Reads seat as free | Not yet checked |
| 2 | Waits at barrier | Reads seat as free |
| 3 | Marks sold and records success | Leaves barrier |
| 4 | Already returned success | Marks sold and records success |
The exact critical section is the combined availability check and state change. Protecting only the increment or only the read is insufficient. The synchronized versions are correct because no second thread can interleave this critical section on the same screen lock; tests support this reasoning but do not prove all schedules by themselves.
## Student B - Compare synchronization strategies
The timing experiment makes 10,000 attempts on 100 seats using eight reusable pool workers, ten trials per strategy, rotating order. Task submission is outside the measured start gate; time includes worker release, booking and waiting for completion. Accepted-return counts use an atomic counter so a lost counting update cannot hide double booking. The unsafe variant deliberately yields between check and set to expose races; its timing is not a fair pure-lock-overhead comparison.
A synchronized instance method locks this. The block version locks a private per-screen object and covers only check/set. Here both critical sections contain essentially the same work, so small timing differences mostly reflect noise and runtime effects rather than a general performance advantage. Static synchronized locks the Class object, appropriate for truly class-wide shared state; it would unnecessarily serialize all screens if seat state is per screen.
## Joint deployment decision and alternatives
For 20 screens, use one lock/state owner per screen, with a small atomic booking section and no network/payment calls inside it. Across multiple application processes, a Java monitor is insufficient: enforce uniqueness and transactions in the authoritative database. Coordinate payment confirmation, holds and cancellations with idempotent booking IDs.
AtomicBoolean[seat].compareAndSet(false,true) replaces check/set with a per-seat atomic transition; use an atomic total counter if needed. The runnable alternative uses AtomicIntegerArray to avoid one object per boolean. ReentrantLock uses lock/try/finally/unlock and supports timed/interruptible acquisition. ConcurrentHashMap.putIfAbsent(seat, bookingId) gives expected O(1) lookup/claim and stores only booked seats. Each bounded-array operation has O(1) state work without waiting, but contention introduces unbounded scheduling/wait time; CAS is not a blanket guarantee that every thread completes within a fixed time. Space is O(s) for s seat flags/atomics, O(1) extra for one screen lock, or O(k) map entries for k booked seats, with object overhead. ConcurrentMap design still needs the correct screen+seat composite key.

## Measured booking timings
Preparation-host medians over ten trials; 10,000 attempts per trial.

| Strategy | Median ms | Total extra sales across 10 trials |
|---|---|---|
| unsafe | 9.432 | 2 |
| method | 8.723 | 0 |
| block | 5.174 | 0 |
| CAS | 9.824 | 0 |

[CHART:q5]
Unsafe timings include Thread.yield and are only a behavior experiment. All synchronized/CAS trials accepted exactly 100 distinct seat claims.
