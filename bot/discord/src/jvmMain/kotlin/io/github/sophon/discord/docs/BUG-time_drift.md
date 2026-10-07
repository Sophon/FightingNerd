# BUG: time drift - daily report not posted

Status: diagnosed, not fixed. The root cause is the most likely explanation, not yet confirmed - see [Confirming it](#confirming-it).

## Symptom

The daily usage report never reached the stats channels. No error was logged, only:

```
23:59:59  INFO: PostDailyReportService - No archived report yet, nothing to post
00:00:02  INFO: RefreshGlossaryService - 1010 glossary items saved
00:00:12  INFO: RefreshDataService - Tekken_8: 42 characters downloaded
...
```

The wiki and glossary refreshes worked fine on the same night.

## How the report is supposed to work

1. Every command records usage into today's report - `day.json` in the stats feature.
2. The stats feature rolls the day over lazily: whenever anything touches the stats (`DayRolloverService.withCurrentReport`), it compares the stored report's date with `todayUtc()`. If the stored day is older, it is archived into `month.json` and a fresh day starts.
3. At UTC midnight `ReportScheduler` calls `PostDailyReportService`, which asks for the month (triggering the rollover) and posts the newest archived day - the day that just ended.

The design has a hidden requirement: **when the report runs, `todayUtc()` must already return the new date.** Otherwise there's no rollover, nothing new in `month.json`, and step 3 posts nothing.

## What actually happened

```
23:59:59.x  ReportScheduler wakes up             <- should be 00:00:00
            todayUtc() == Oct 6                  <- still yesterday
            day.json date == Oct 6               <- "same day", no rollover
            month.json has no new entry
            getLatestReport() -> null -> "No archived report yet"
00:00:00    the date actually changes - too late
```

The scheduler woke up before midnight, by a second or less. That was enough.

The wiki and glossary schedulers woke up at the same moment (the glossary finished at 00:00:02). They worked because they don't care what date it is - they just download data. **Only date-dependent work is broken by an early wake-up.** That difference is what pointed to the cause.

The bug was silent for two reasons:
- "No report" is treated as a valid state (the very first night has nothing archived), so it logged at INFO and returned `Success`. `ReportScheduler` only logs `onError`.
- If `month.json` had held older days, the bot would have posted the **day before yesterday's** report with no hint that anything was wrong.

## Root cause: two clocks

The JVM has two different clocks, and the scheduler mixes them.

| Clock | Kotlin / JVM | Answers | Can jump? |
|---|---|---|---|
| Wall clock | `Clock.System.now()`, `System.currentTimeMillis()` | "What time is it?" | Yes - NTP corrections, VM clock sync, manual changes |
| Monotonic clock | `System.nanoTime()`, `TimeSource.Monotonic`, coroutine `delay` | "How much time has passed?" | No - only moves forward, but isn't tied to real time |

`untilNextUtcMidnight()` uses the **wall clock** to work out "midnight is X hours away". Then `delay(X)` waits X hours on the **monotonic clock**. Those two only agree if the wall clock doesn't get corrected while the scheduler waits. On a cloud VM (Fly.io), the guest's wall clock does get corrected against the host and NTP. If the wall clock is moved forward by even a few milliseconds during the wait, the monotonic wait still ends at the originally planned moment, which is now before the corrected midnight.

```
wall clock:       ...23:59:58 ── correction (+Δ) ──> 23:59:59.x ── 00:00:00
monotonic delay:  started at T, ends at T + X  ─────────────────────┘ (ends here, Δ too early)
```

### The second drift: fixed delay vs fixed rate

`Scheduler` runs the task, then waits the full period:

```kotlin
delay(initialDelay)
while (true) {
    emit(task.invoke())
    delay(period)   // counted from when the task ENDED
}
```

That's **fixed delay**: each run starts `period` after the previous run *finished*. The start time moves later by the task's duration every cycle. The wiki refresh takes about 4 minutes, so its next run starts around 00:04, the one after around 00:08, and so on. (All three started together on the night above, so it was most likely the first cycle after a deploy or restart - not verified.)

The alternative is **fixed rate**: each run starts `period` after the previous run *started*. That stops the creep from task duration, but it still counts 24 h on the monotonic clock, so it can still drift from wall-clock midnight.

Neither matters for "refresh roughly once a day". Both matter for "run right after the date changes".

## Lessons

1. **Wall clock for "when", monotonic for "how long".** Measuring how long something took with the wall clock is wrong (the clock can jump). Waiting until a wall-clock time with only a monotonic wait is also wrong (the clocks drift apart). If the target is a wall-clock moment, check the wall clock again when you wake up.
2. **Re-anchor long schedules.** Don't compute the target once and add `24.hours` forever. Recompute "time until the next midnight" every cycle, so errors can't build up.
3. **Never schedule exactly on a boundary.** Anything running *at* midnight that depends on *being past* midnight is a race. Add a grace margin (e.g. +1 minute) or make the consumer tolerant of running on either side.
4. **Make implicit ordering explicit.** The report depended on a side effect (the lazy rollover) happening first, and on the clock agreeing. A sturdier contract is "post the report **for date D**": the service knows which day it wants and can check that it got that day.
5. **Separate "expected empty" from "unexpected empty".** "No report on the very first night" and "no report when one should exist" took the same silent `Success` path. When an empty result can be a symptom, log it as a warning or return an error.
6. **Compare with siblings that work.** The wiki scheduler working wasn't a contradiction. It was the clue that the timing was wrong for everything, but only date-dependent work noticed.

## Proposed fix

- **Main:** `Scheduler` re-anchors every cycle - `delay(untilNextUtcMidnight() + grace)` in the loop instead of a fixed `24.hours`. This fixes all three midnight schedulers at once.
- **Optional:** `PostDailyReportService` asks for yesterday's report explicitly and warns when it's missing, instead of taking `month.json.lastOrNull()`.

## Confirming it

The diagnosis fits the logs, but no controlled test has proved it yet.

- On the Fly machine, inspect `day.json` and `month.json` in the stats directory. If yesterday's day reached `month.json` only after the first command after midnight (not at the scheduled run), the early wake-up is confirmed.
- Temporarily log `Clock.System.now()` at the start of the report task to see how early it wakes up each night.
