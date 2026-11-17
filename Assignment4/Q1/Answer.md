# Q1 - Handling failure in a bank ATM simulation
## Shared implementation
AtmDemo simulates a balance of Rs. 5,000 and a local card file containing account 1001. It reads account and amount from the console in interactive mode; its default mode runs seven repeatable cases. BigDecimal avoids binary floating-point money errors. All values are fake. Cash dispensing is represented by a printed message, not hardware interaction.
## Student A - Exception design
[DIAGRAM:Q1]
| Failure point | Exception type | Classification | Catch location |
|---|---|---|---|
| Account/amount contains letters | NumberFormatException | Unchecked | Multi-catch in withdraw |
| Zero/negative amount or excessive decimals | InvalidAmountException | Unchecked | Specific amount handler |
| Amount exceeds balance | InsufficientFundsException | Checked | Funds handler |
| Card file missing | NoSuchFileException | Checked IOException subtype | Multi-catch |
| Other file access failure | IOException | Checked | Final I/O handler |
| Unknown account | IllegalArgumentException | Unchecked | Account handler |
| VM exhaustion | OutOfMemoryError | Error; unchecked | Not treated as a recoverable withdrawal |
The multi-catch types NumberFormatException and NoSuchFileException are unrelated, so that union is legal. NumberFormatException and InvalidAmountException must be caught before their superclass IllegalArgumentException. NoSuchFileException must precede IOException. Finally runs after normal completion or exception propagation of this try statement and prints simulated session cleanup. It cannot promise cleanup after VM termination, System.exit or power loss, and it does not reverse an already-completed financial transaction.
## Student B - Tests and tracing
| Case | Input/path | Observed handler | Finally ran? |
|---|---|---|---|
| 1 | 1001, 500, existing card | SUCCESS; balance 4500.00 | Yes |
| 2 | abc, 500 | MULTI_CATCH: NumberFormatException | Yes |
| 3 | 1001, abc | MULTI_CATCH: NumberFormatException | Yes |
| 4 | 1001, -10 | INVALID_AMOUNT | Yes |
| 5 | 1001, 6000 | INSUFFICIENT_FUNDS | Yes |
| 6 | Missing card-reader file | MULTI_CATCH: NoSuchFileException | Yes |
| 7 | 9999, 100 | BAD_ACCOUNT | Yes |
Full output is in evidence/AtmDemo.txt. UncaughtDemo intentionally terminates with NumberFormatException. In its trace, the header names the main thread, exception and invalid value; NumberFormatException.forInputString constructs the exception; the two Integer.parseInt frames show library parsing calls; UncaughtDemo.parseAmount at line 2 identifies the application fault; UncaughtDemo.main at line 3 is the application entry point. Stack traces list the most recent call first. Library line numbers can vary with JDK version.
BroadCatch.java is intentionally excluded from the normal build. Compiling it separately produces "exception FileNotFoundException has already been caught": catch(Exception) already covers that subtype, making the following catch unreachable. The actual compiler transcript is included.
## Joint decision and alternative
Do not replace the specific handlers with one generic Exception catch inside the ATM logic: it loses distinct user remedies and can hide programming faults. A final application-boundary catch can log unexpected Exceptions, show a generic failure and preserve diagnostics, but it should not claim recovery from every Error.
Without exceptions: IF NOT scanner.hasNextLong() THEN return INVALID_ACCOUNT; read account; IF NOT scanner.hasNextBigDecimal() THEN return INVALID_AMOUNT; read amount; validate range, identity, funds and file status; return a typed result. hasNextInt is suitable for integer-only inputs, but money and account ranges may require other token methods. Prechecking file existence does not remove a race before opening it, so real I/O still requires failure handling. Error codes need checks at every caller and can be ignored; an enum/result object is clearer than multiple meanings of -1.
Input parsing costs O(L) for L characters in either approach. Returning a primitive code adds O(1) time/space. A normal Java exception typically captures a stack of depth d in O(d) time and memory; unwinding/handler search also depends on depth. A non-thrown path does not incur that trace allocation. Exact exception costs depend on JVM optimizations and should not be inferred from Big-O alone.
