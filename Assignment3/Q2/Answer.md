# Q2 - Custom exceptions and propagation in an examination portal
## Student A - Hierarchy and layer design
[DIAGRAM:Q2]
ExamException extends Exception. WindowClosedException, NotRegisteredException, AnswerTooLargeException and SubmissionStorageException are checked subclasses because callers must choose how to respond to expected submission failures. Invalid programmer arguments could instead use unchecked IllegalArgumentException. The supplied StorageDemo retains the original assignment class hierarchy; PortalDemo adds the remaining rules.
The controller checks the exam window; the service checks registration and the 500 KiB answer limit; storage copies the validated file. service declares throws ExamException, and storage declares throws SubmissionStorageException. A storage IOException is wrapped with its cause, propagates through service and is caught at the controller. The student sees "Could not store submission" while the diagnostic trace retains the low-level path and cause. This separates a stable domain message from filesystem details.
[DIAGRAM:Q2PATH]
## Student B - Failure scenarios and chaining
| Scenario | Observed result |
|---|---|
| Open window, registered, small file | Submission accepted |
| Closed window | WindowClosedException |
| Unregistered student | NotRegisteredException |
| 500 KiB + 1 byte file | AnswerTooLargeException |
| Destination parent directory absent | SubmissionStorageException, caused by NoSuchFileException |
PortalDemo asserts these five results and prints the complete chained trace. getCause() returns the original IOException subtype, retaining its message and stack. Constructing new ExamException(e.getMessage()) instead yields getCause() = null: the original exception type, origin stack and causal relationship disappear. The deliberate demonstration prints both cases. File-size checking is an application rule and does not prevent a concurrently changing source; robust production storage should bound the actual streamed bytes and publish atomically.
## Completed scaffold
StorageDemo replaces only TODO 1 with throw new SubmissionStorageException("Could not store " + fileName, e). Actual output matches:
```text
Saved answer01.pdf
Error: Could not store answer02.pdf
Cause: Disk quota exceeded
```
If the wrapper has no cause, getCause() is null. The original scaffold then calls null.getMessage(), so it throws NullPointerException while constructing the Cause line; it does NOT print "Cause: null". A null-safe diagnostic could print that, but the given code is not null-safe.
## Joint conclusion and unchecked alternative
A meaningful storage exception lets the UI handle a submission failure without knowing the filesystem API, while retaining the original cause for maintenance. Do not show raw internal paths or full stack traces to ordinary users in a production portal; the local exercise prints them as diagnostic evidence.
If ExamException extends RuntimeException, callers are no longer forced to catch or declare it. The same throws clauses may remain as documentation, and the controller can still catch it, but compilation no longer enforces handling. Throwing and stack-capture cost is broadly the same for checked and unchecked exceptions with normal stack traces. A wrapper retains its own O(d1) trace plus a reference to the cause's O(d2) trace; a chain of k exceptions retains the sum of their trace/message memory, not merely one reference's size. The cause object is retained, not deep-copied.
