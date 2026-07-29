# Bot TODO

Post-app-release refactor menu for the Discord bot. Nothing here is urgent —
this file is the ordered list of "solid foundations" work identified during
Phase 3 of the mobile system design interview (Chapter 3 / News Feed applied
to Fighting Nerd). Sequenced after the app release.

## Database

### Migrate wiki data from in-memory Map to SQLDelight (DB-as-SoT)

Currently `Map<characterId, List<Move>>` + `Map<aliasOption, characterId>` are
the SoT for wiki data. On restart (every deploy), commands return "no data"
until the next refresh tick — up to a 6h dark window per deploy.

**Refactor:**

- Persist wiki data in bot-local SQLDelight
- Drop the primary Map (snapshot reads via `suspend fun` are fine — the bot
  is request-response, no `Flow` observation needed)
- Aliases become a proper table `character_alias(character_id, alias)` with
  index on `alias`. Kills the alias Map.

Speed argument for keeping the Map doesn't hold at 300 uses/day scale —
SQLite with warm page cache is sub-ms, dwarfed by Discord's 100ms+ round
trip. The Map+DB pattern isn't justified until 100x-1000x scale or expensive
derived state.

### Shared: adopt `WikiClient` observe/refresh interface

Cross-module change in `core/wiki/model/WikiClient.kt` (see
`composeApp/TODO.md` for the app-side driver of this refactor).

Bot's use cases adapt by calling `.first()` on the Flow:

```kotlin
class GetMovesUseCase(private val client: WikiClient) {
    suspend operator fun invoke(charId: String, filter: Filter): List<Move> =
        client.observeMoveList(charId, filter).first()
}
```

Intent (snapshot vs observation) lives at the use-case layer; `WikiClient`
stays plumbing.

## Remote / download orchestration

### Fix `SyncWikiDataUseCase.kt` L28-33 — timeout aborts cross-wiki loop

```kotlin
if (characterListResult == null) {
    errors.add(BotError.DownloadError("wiki host unresponsive — skipping remaining games"))
    break   // ← stale guard
}
```

`downloadCharacterList` returns `null` only on `withTimeoutOrNull` firing. So
a single wiki's character-list *timeout* aborts the entire outer loop across
all wikis. Every other error path already collects and continues.

The `break` is a stale guard from an earlier version. The intended
post-refactor behavior ("one wiki shouldn't break the chain") is honored on
every path except this one.

**Fix:** replace `break` with continue-to-next-wiki.

### Structured multi-error report

Currently `errors.first()` is returned at the end — every subsequent error is
discarded. If DustLoop and Wavu both fail with different reasons, only one
surfaces.

**Fix:** log all errors with structured context (per-wiki, per-phase),
surface first as the primary result. Cheap observability win.

## Domain

### Ban eviction — query-time filter

Default duration is 30 days but eviction may not be implemented. Ban rows
accumulate; expired bans stay effective past their intended duration.

**Fix:** query-time filter — every ban lookup runs `WHERE banned_until >
now()`. Correctness without infrastructure. Optional background sweep later
to reclaim disk if the table grows.

## Testing

### Integration test per wiki: URL resolution

URL resolution logic inside `downloadMoveListFor` is AI-generated per wiki
and isn't consistently owned. If one wiki silently changes its API, no
failing test surfaces the regression.

**Fix:** one integration test per wiki asserting "downloading character X
produces a `Move` with a resolvable image URL." Cheap defense against silent
per-wiki drift.

## Open questions — resumption point

**Topic 8 (Error handling)** was the next topic when the session paused.
News Feed's shape is:

- Remote source maps transport errors to `RemoteError`
- Use case merges local + remote errors into domain hierarchy (`FeedError`,
  `PostError`)
- ViewModel splits errors into **state** (persistent — empty screen + retry)
  vs **event** (transient — toast/banner)
- Same underlying error can be state or event depending on current state
  (empty cache vs stale cache)

FN already has a layered error surface: `RemoteError`, `LocalError`,
`WikiError`, `BotError`, `AppError`, `SettingsError`, `DataError`.

**To ask next session:**

1. Does the bot distinguish command-shaped errors (unknown character, unknown
   move) from transport-shaped errors (wiki down, timeout) at the response
   layer? Or are they all the same "error embed" shape?
2. How thick is the error hierarchy? Is the mapping Remote → Wiki → Bot
   clean, or has it accumulated leaky abstractions?
3. For scheduled refreshes: does the bot surface refresh failures to admins
   somehow, or are they only in logs? (Ties back to the multi-error report
   item above.)

## Discussion & discoveries — Phase 3 recap

### Bot writes: local SQLDelight + UPSERT is right

News Feed's persistent queue + idempotency-header machinery is N/A for the
bot. No client-server network hop, no flaky network between user command and
DB write. `REPLACE ON CONFLICT` handles idempotency at the SQL layer — right
tool for the shape. Discord retries, user double-clicks, bot restarts all
resolve safely.

### Bot doesn't need Flow observation

Bot's read pattern is request-response, not persistent observation. Snapshot
reads via `suspend fun` are the right shape. Move to DB-as-SoT is for
durability, not for reactivity.

### Per-game DB architecture holds up

Divergent per-wiki schemas justify per-game DBs over a shared DB with
wide-null schema or JSONB blobs.

### Wiki-priority tiebreaker is silent

"First successful character match" makes wiki registration order a
load-bearing tiebreaker for ambiguous names (e.g. Mai in SF vs CoTW vs KOF).
User handles disambiguation via `fd<game>` prefix commands or slash autocomplete
with game annotation. Worth documenting explicitly that the default fd
resolution is order-dependent.

### Speed vs durability trade-off

The Map's speed advantage over DB is nanoseconds vs microseconds —
imperceptible at 300 uses/day when Discord's round-trip is 100ms+. The
durability gap (0-6h dark window per deploy) is a real bug, not a
"tradeoff."
