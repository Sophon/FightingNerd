# Bot data policy

How the Discord bot stores, refreshes, and evicts wiki data. Written in plain terms — the actual mechanics live in `core/src/commonMain/kotlin/io/github/sophon/core/wiki/data/` (`CharacterRepo`, `MoveRepo`, `Adapters`) and `bot/discord/src/jvmMain/kotlin/io/github/sophon/discord/feat/core/domain/Scheduler.kt`.

## Where data comes from

Every piece of fighting-game data is scraped from an open-source wiki (Wavu, DustLoop, SuperCombo, DreamCancel, Mizuumi, DragDown, XKO). 
The wiki is the source of truth; the bot only holds a cached copy so it can answer Discord queries without hammering the wiki on every request.

EWGF is different — it's the Tekken ELO service. The bot keeps a small SQLite table pairing Discord user IDs with Tekken player IDs, so users can `/register` once and then query their own stats.

## Where data lives

- **Wiki data** — SQLite file on the bot's disk. One database, tables per wiki feature. (Previously held in-memory `Map<>`s; switched to file mode so restarts don't force a cold re-download of everything.)
- **EWGF registrations** — separate SQLite file. Only user-generated content the bot keeps.
- **Nothing else is persisted.** No message logs, no query history stored per-user.

## When data refreshes

The bot uses **scheduled polling** (also called *periodic pull*) — no push, no webhooks; the bot decides when to re-scrape.

Each wiki feature (`WavuWikiDiscordFeature`, `DustLoopWikiDiscordFeature`, …) starts a `Scheduler` on bot boot:

- **Initial refresh** — fires immediately on `start()`.
- **Periodic refresh** — every `TIME_UPDATE_INTERVAL_H` hours after that, forever, while the bot is up.

The scheduler is a simple `flow { delay(...); while(true) { emit(task()); delay(period) } }` — no cron, no persistence of "last run at." A restart resets the clock.

The refresh itself (`BaseWikiClient.refreshData()`):
1. Download the character list from the wiki.
2. For each character, download that character's move list.
3. Write everything through the shared `CharacterRepo` / `MoveRepo` (see stale handling below).

A `/refresh` admin command can force this on demand.

## Stale handling — strike-based eviction

The eviction policy is **strike-based** (a.k.a. *N-strike eviction* / *tolerant eviction* — the "three strikes and you're out" pattern applied to cache rows). 
Each cached row carries a `failure_count`; misses add strikes, exceeding the threshold evicts.
The naive alternative — "on refresh, wipe the table and re-insert" — breaks the moment the wiki returns an empty page or a partial response, so we don't do that.

The refresh runs as an **atomic refresh** — the whole sequence below is wrapped in a single `dbAdapter.transaction { ... }`, so either every step lands or none of them do. 
`CharacterRepoImpl.refreshCharacterList()` and `MoveRepoImpl.refreshMoveList()` both execute:

1. **Download from the wiki.** If the download errors out, return the error and touch nothing — keep the existing cache (**fail-open on error**).
2. **If the response is empty, no-op** (**empty-response guard** / *stale-if-empty*). Empty is treated as "upstream isn't giving us anything right now," not "delete everything." The existing rows stay.
3. **Upsert every row that came back.** Any row present in the response has its data refreshed.
4. **Increment `failure_count` for every locally-cached row that was *absent* from the response.** This is the **strike increment** (the "error increment") — each row tracks how many consecutive refreshes have failed to see it.
5. **Delete any row whose `failure_count` exceeds the threshold.** Threshold is `FAILURE_COUNT_THRESHOLD = 5`, defined in both repos.

Effect: a move has to disappear from the wiki for **5 consecutive refresh cycles** before the bot drops it. A one-off missing page, a transient outage, a scraper glitch — none of them evict good data.
But if the wiki genuinely retires a character or a move, it does get cleaned up eventually.

A row that reappears is upserted (step 3) — inserts reset the count via the SQL, so a single successful appearance forgives past misses (**strike reset on success**).

## What clears the cache

- **Disable-and-purge** — `WikiClient.clearCache()` wipes both characters and moves for that feature. Called when a feature is disabled via admin config (mirrors the app's disable flow).
- **Nothing on graceful shutdown** — the file DB persists between restarts. First refresh on next boot fills in whatever changed while the bot was down.

## Failure modes worth knowing

- **Wiki totally down at boot** — the initial refresh errors; the cache is whatever survived from the last run (empty if this is a fresh install). Users get an error until the next refresh cycle succeeds.
- **Wiki returns partial data** — protected. Missing rows just tick their failure counter; they don't get wiped until they've been absent 5 times.
- **DB write fails mid-refresh** — atomic refresh (see above) rolls back the partial write. The cache stays consistent, the repo returns `DataError.Local.UNKNOWN`.
- **Bot restarts mid-refresh** — same as above, nothing partial lands.
