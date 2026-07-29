# App TODO

Post-app-release refactor menu for `composeApp`. Nothing here is urgent —
this file is the ordered list of "solid foundations" work identified during
Phase 3 of the mobile system design interview (Chapter 3 / News Feed applied
to Fighting Nerd). Sequenced after the app release.

## Remote / download orchestration

### Fix atomic-refresh gap

Stated invariant: "cache gets rewritten only when full successful data is
retrieved." Actual code (`HomeVM.loadMoveList`): **per-character** atomicity.
Successful characters commit; failed ones are logged only. A swipe-refresh
that gets 28 of 30 characters writes 28 fresh + keeps 2 stale — mixed state.

**Fix:** either implement per-game atomicity to match the stated invariant,
or explicitly document per-character atomicity as intentional and correct
the mental model. Write a test asserting whichever invariant is chosen.

### Add global concurrency cap

`HomeVM.loadWidgetData` fans out per-game via `launch` with **no cap**;
per-character within a game uses `Semaphore(MAX_PERMITS = 2)`. Effective
ceiling: N_games × 2 with no global limit. 5 enabled games → up to 10
concurrent HTTP requests hitting wiki hosts.

**Fix:** add a global semaphore or a shared dispatcher with a concurrency
limit across all game+character downloads. Reduces rate-limit / IP-ban risk
from wiki hosts (many run on shared MediaWiki infrastructure).

### Add timeouts

App-side wiki requests are currently unbounded. A hung request hangs the
entire loading state until the user backgrounds the app.

**Fix:** `withTimeoutOrNull(...)` around every wiki request path, analog to
the bot's `REQUEST_TIMEOUT_S`.

### Make `SaveFeatureConfigUseCase` proactively trigger downloads on enable

Current behavior (`SaveFeatureConfigUseCase.kt` L36-43): only wipes cache
for newly-disabled games and saves prefs. **No download trigger.** The
"enabled → downloaded" invariant is propped up by the navigation invariant
"all paths to Move List pass through Home" (which then fires
`loadWidgetData(added)`).

Fragile — breaks under any future addition that lets the user land on Move
List without transiting Home: deep links, notifications, saved routes,
iOS Spotlight, Android widgets, "quick access" features.

**Fix (pick one):**

- `SaveFeatureConfigUseCase` proactively kicks off downloads for
  newly-enabled games (aligns with the "explicit Save gates the costly
  op" mental model)
- OR every wiki-data screen ensures-data-present on entry (defensive at
  every consumer)

## Database

### Adopt `WikiClient` observe/refresh interface (staged)

Currently `WikiClient` exposes `suspend fun fetch…`, giving VMs snapshots.
Under future periodic auto-refresh, the stale-VM problem surfaces: VM holds
a cached snapshot, DB has refreshed data, UI doesn't update until
re-navigation.

Cross-module change in `core/wiki/model/WikiClient.kt` — bot also adopts
(see `bot/discord/TODO.md`).

**Agreed shape:**

```kotlin
interface WikiClient {
    fun observeMoveList(characterId: String, filter: Filter): Flow<List<Move>>
    suspend fun refreshMoveList(characterId: String): Result<Unit, WikiError>
    // no `fetch` — bot's use cases call observe(...).first()
    // no `writeX` — refresh IS the write for wiki data
}
```

Bot's use cases call `.first()`; app's use cases pass Flow through. Intent
lives at the use-case layer; `WikiClient` stays plumbing.

**Staged rollout:**

1. **Adopt signature split FIRST** — `WikiClient` exposes `observeX(): Flow`,
   initially backed by `flowOf(dbSnapshot)`. Callers migrate to
   Flow-consuming shape.
2. **Swap Flow impl to `SQLDelight.asFlow()`** — reactive observation for
   free, no caller changes.

Per-game DB architecture is fine with Flow: each game's DB emits its own
Flow; cross-game screens use `combine(flow1, flow2, ...)`.

### Aliases → proper DB table

Move alias resolution from a derived in-memory Map to a schema-level table:
`character_alias(character_id TEXT, alias TEXT, PRIMARY KEY(character_id, alias))`
with index on `alias`. Lookup becomes indexed SQL:
`SELECT character_id FROM character_alias WHERE alias = ?`.

Same schema change lives cross-module in `core/wiki/data`.

## Media & storage

### Refactor image storage to per-game directories

**Problem — two caches with mismatched invariants:**

- **Text data** (SQLDelight): user-controlled, atomic-refresh, offline-first ✓
- **Media** (Coil default: ~2% memory / ~2% disk cap, up to 250MB):
  LRU-under-pressure, network-fallback ✗

A user on a plane with 5 games enabled has frame data guaranteed but images
subject to Coil's discretion. Since images are functional content (move
recognition + hitbox visualization), the invariant mismatch matters.

**Refactor:**

- Per-game image storage on disk you own, not Coil's cache:
  `${appDataDir}/wiki-images/${gameId}/${filename}`
- Wiki client's download step fetches images alongside move data
- `Move` domain object stores local file path (not remote URL)
- Coil is fed `File(path)` — renderer + memory cache only, no disk-cache role
- On game disable: delete `${appDataDir}/wiki-images/${gameId}/` (analog to
  `clearCache()`)
- On refresh: redownload images. Atomic-refresh invariant now covers both
  text and media.

**Sub-decisions to make before implementing:**

- **Atomicity: joined or split?** Lean: text updates first, images stream in
  async. Don't block feature visibility on bytes. Show a placeholder for
  still-loading images.
- **Enable-time cost.** ~1.5GB for 5 games ballpark (5 games × ~6000 images
  × ~50KB). UX must communicate download progress and disk footprint.
  Eventually needs a "Manage storage" screen.
- **Refresh diff strategy.** URL-identity is enough for wiki data change
  frequency (months, not minutes). Skip hash-based diffing.

Related: **Chapter 8 (Google Drive)** in the prep book has real overlap —
media storage strategies, resumable uploads, block-level sync. Different
shape (Drive is user-uploaded content), but the download / storage / refresh
discussion transfers. Worth reading ahead during next Phase 1.

### Interim: configure Coil error/placeholder

Until the per-game storage refactor lands, at minimum set `error(...)` and
`placeholder(...)` on image requests so failures degrade visually rather
than showing nothing.

## Open questions — resumption point

**Topic 8 (Error handling)** was the next topic when the session paused.
News Feed's shape is:

- Remote source maps transport errors to `RemoteError`
- Use case merges local + remote errors into domain hierarchy
- ViewModel splits errors into **state** (persistent — empty screen + retry)
  vs **event** (transient — toast/banner)
- Same underlying error can be state or event depending on current state
  (empty cache vs stale cache)

FN already has a layered error surface: `RemoteError`, `LocalError`,
`WikiError`, `AppError`, `SettingsError`, `DataError`. `HomeVM` calls
`overlayService.show(error)` on failures — that's the toast/event pattern.

**To ask next session:**

1. **Does the app distinguish state-error from event-error?** Concretely:
   on Move List, if DB is empty AND refresh fails, is that empty-screen-with-
   retry (state) or a toast-over-blank-screen (event)? Or do both cases
   produce the same UI?
2. **How thick is the error hierarchy?** Is the mapping Remote → Wiki → App
   clean, or has it accumulated leaky abstractions (raw `IOException`
   reaching the VM)?
3. **Does the empty-cache vs stale-cache distinction get made anywhere?**
   News Feed's argument was that the same underlying error should produce
   different UX depending on current state — is FN doing this, or does
   refresh-failed always produce the same UI?

Resume by asking user for their recollection, then verifying against code
(start with `HomeVM`, `MoveListVM`, and any `AppError` mapping).

## Discussion & discoveries — Phase 3 recap

### Architecture split holds up

CMP-shared UI is justified: identical UI both platforms, no camera/BT/native
components, custom transition animations, user doesn't know Swift/SwiftUI.
Platform-specific slots are minimal — Ktor driver, SQLDelight driver, file
ops.

Untested (worth revisiting at some point): the classic CMP paper cuts — IME
behavior, scroll momentum on iOS, focus / D-pad, screenshot testing per
platform. Not been bitten yet.

### App has zero network writes today

No analytics, no feedback, no bug reports. News Feed's persistent queue +
idempotency-key machinery is entirely N/A for FN's current shape. If/when
app-side writes are added (feedback flow, telemetry), revisit — the machinery
kicks in when writes cross a network to state the app doesn't own.

### The intent-vs-code through-line

Across topics, the same shape kept surfacing: the code doesn't yet reflect
the mental model. Instances:

- Atomic-refresh invariant intended → per-character atomicity in code
- `SaveFeatureConfigUseCase` intended to trigger downloads on enable →
  actually doesn't
- Observe/refresh split intended → not implemented
- Offline-first for images intended → Coil defaults in code

Underlying architectural decisions (KMP/CMP split, per-game DBs, hexagonal
integration boundary, MVVM + usecases) hold up well. This is a **discipline
refactor, not a rewrite.**

### Two caches, mismatched invariants (topic 7)

Text data (SQLDelight): user-controlled, atomic-refresh, offline-first ✓
Media (Coil default): LRU-under-pressure, network-fallback ✗

The whole point of the media refactor above is aligning these under the
"offline-first for text-and-media" invariant.

### AI-generated URL resolution is a small silent risk

URL resolution logic inside `downloadMoveListFor` is AI-generated per wiki
and inconsistently owned. If one wiki changes its API, no test catches the
regression. Integration test per wiki is the cheap defense (see
`bot/discord/TODO.md`).

### Bot durability drives cross-module refactor

Bot moving to DB-as-SoT (see `bot/discord/TODO.md`) is what triggers the
shared `WikiClient` interface change. App-side reactive observation is the
long-term goal, but the signature split can land first without waiting for
the DB-layer swap.
