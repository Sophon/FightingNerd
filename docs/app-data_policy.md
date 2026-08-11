# App data policy

How the mobile app (Android + iOS) stores, refreshes, and evicts wiki data. Plain-language companion to [app-database.md](./app-database.md), which covers the schema-migration side.

The shared refresh/eviction mechanics live in `core/src/commonMain/kotlin/io/github/sophon/core/wiki/data/` (`CharacterRepo`, `MoveRepo`, `Adapters`) — the same code the bot uses. 
What differs is *when* the app triggers a refresh, and what the user controls.

## Where data comes from

Every piece of fighting-game data is scraped from an open-source wiki (Wavu, DustLoop, SuperCombo, DreamCancel, Mizuumi, DragDown, XKO). 
The wiki is the source of truth; the app holds a cached copy on-device so it works offline and doesn't hammer the wiki on every screen open.

## Where data lives

- **Wiki cache** — SQLDelight (SQLite) file on the device. Two databases: one for characters, one for moves.
- **Feature preferences** — `DataStore<Preferences>`. Which wikis (games) the user enabled or disabled.
- **Nothing user-generated is persisted.** No account, no accompanying cloud sync, no telemetry stored per-user.
- **Backups excluded.** `android:allowBackup="false"` + `dataExtractionRules` keep the DB out of Google Drive / device-to-device transfer. A restore would put back a stale schema on what looks like a fresh install and defeat the migration logic.

## When data refreshes

The app uses **on-demand refresh** (also called *lazy refresh* / *pull-on-read*) — no background scheduler, no periodic pull like the bot has. Refreshes happen only on explicit triggers:

- **First launch of an enabled feature** — the app verifies data presence and downloads if the cache is empty.
- **Toggling a feature on** — same flow. `SaveFeatureConfigUseCase` marks the feature enabled; downloading is triggered when the user next visits it.
- **Manual refresh** — user action inside the app.

Invariant (**enabled-clean invariant**, documented in `SaveFeatureConfigUseCase`): *an enabled feature must have clean, complete data*. 
Corrupt/partial data on an enabled feature is unacceptable; being disabled and having to re-download on re-enable is fine.

## Stale handling — strike-based eviction

The eviction policy is **strike-based** (a.k.a. *N-strike eviction* / *tolerant eviction* — the "three strikes and you're out" pattern applied to cache rows). Each cached row carries a `failure_count`; misses add strikes, exceeding the threshold evicts.
The naive alternative — "on refresh, wipe the table and re-insert" — breaks the moment the wiki returns an empty page or a partial response, so we don't do that. Same code as the bot; the app just triggers it on demand instead of on a schedule.

The refresh runs as an **atomic refresh** — the whole sequence below is wrapped in a single `dbAdapter.transaction { ... }`, so either every step lands or none of them do. 
`CharacterRepoImpl.refreshCharacterList()` and `MoveRepoImpl.refreshMoveList()` both execute:

1. **Download from the wiki.** If the download errors out, return the error and touch nothing — keep the existing cache (**fail-open on error**).
2. **If the response is empty, no-op** (**empty-response guard** / *stale-if-empty*). Empty is treated as "upstream isn't giving us anything right now," not "delete everything."
3. **Upsert every row that came back.**
4. **Increment `failure_count` for every locally-cached row that was *absent* from the response.** This is the **strike increment** (the "error increment") — each row tracks how many consecutive refreshes have failed to see it.
5. **Delete any row whose `failure_count` exceeds the threshold.** Threshold is `FAILURE_COUNT_THRESHOLD = 5`.

Effect: a move has to be missing across **5 consecutive refresh cycles** before the app drops it. Transient scraper glitches don't evict good data; genuine wiki removals do get cleaned up eventually.
A row reappearing gets upserted and its counter reset (**strike reset on success**).

## What clears the cache

- **Disable-and-purge** — `SaveFeatureConfigUseCase.wipeCacheForFeature()` calls `WikiClient.clearCache()`, which drops both character and move rows for that game via `CharacterRepo.wipeData()` + `MoveRepo.wipeData()`. This is why the feature-config UI requires a confirm dialog.
- **Wipe-failure fail-safe** — if `clearCache()` errors, the code logs a warning and still marks the feature disabled in prefs (the enabled-clean invariant is what matters; residue in a disabled feature is fine). Re-enabling later triggers a fresh download that overwrites whatever residue is left.
- **Destructive migration on app upgrade** — schema-version bump drops all tables, cached wiki data re-downloads on next launch (see [app-database.md](./app-database.md)). Safe because the DB is purely a cache — no user-generated content lives here.
- **User uninstall** — normal OS behavior. **Backup-excluded** (`android:allowBackup="false"` + `dataExtractionRules`), so no cloud restore puts a stale DB back.

## What the user controls

- **Which features are enabled** — from the Settings screen. Enabling triggers a download; disabling wipes the cache for that game (with confirm).
- **Manual refresh** — force a re-pull from the wiki without disabling.
- **Uninstall** — the only way to wipe everything at once.

There's no per-character or per-move eviction the user can trigger; staleness is handled automatically by the failure-count pattern above.

## Failure modes worth knowing

- **Wiki down at first launch** — the download errors; feature stays enabled but empty. Next successful refresh fills it in.
- **Wiki returns partial data mid-use** — protected. Missing rows just tick their failure counter; nothing is wiped until 5 consecutive misses.
- **DB write fails mid-refresh** — atomic refresh (see above) rolls back the partial write. The repo returns `DataError.Local.UNKNOWN` and the previous cache stays intact.
- **App killed mid-refresh** — same as above, nothing partial lands.
- **App upgraded with a schema change** — destructive migration wipes and re-downloads on next launch. The user sees a one-time loading state.
