# Wiki module

- hexagonal + DDD
  - inspired by Tom Homberg's Get Your Hands Dirty on Clean Architecture
- we should aspire to have a deep module, not a wide module
- the priority is good architecture and best practices, not what's already in the code
  - we can do drastic changes

## Design
- do we need `adapter/in`? or should we simply call the `port/in`?
  - no - the hosts are the inbound adapters (see Findings)
- `port/out`
  - ktor - REST requests from wikis
  - sql - store frame data of moves and characters
- SQL
  - one database for all wikis
  - `move` and `character` tables have invariants that are different per game
  - ID? with one DB instead of DB per module, we will prob need `move-char-game` as part of ID
  - char : move is `1:n`
  - while technically possible, char : game is **not** `n:n` - crossovers (like Mai in SF, KOF and COTW) are considered different chars
- ports
  - inbound - usecases
  - outbound - ports
  

## Plan

1. design the interface - how the outside interacts with this module
2. move what can be moved from `core` - mostly wiki stuff
3. move a single-game wiki module (`wavu`) to `wiki`
    - a big refactor - single SQL database
4. move a multi-game wiki module (`dustloop`) to `wiki`
5. move the rest of the wikis
6. extensive testing

## Current plan

### Test run
- both hosts (app and bot) configure the wiki module from their JSON files
  1. the host loads its JSON file
  2. the host maps it into `WikiConfig` via `WikiConfig.create(...)`
  3. the host calls `ConfigureWikiUseCase(wikiConfig)`
  4. the in-memory adapter holds the config
- nothing crashes; the host only ever sees a `WikiConfig` or a `WikiError`
- the host owns the config; the wiki only receives it
  - app - available features from `modules.json` (compose resource), enabled/disabled from DataStore
  - bot - available features from `config.json` (file); everything available is enabled
- each host has its own mapper - host configs can differ, the wiki only ever sees `WikiConfig`
  - no JSON crosses into the wiki

```kotlin
// app - after its async config load
val result = availableFeatures.toWikiConfig(enabledGames = dataStoreEnabledGames) // WikiConfig.create(...)
    .flatMap { wikiConfig -> configureWikiUseCase(wikiConfig) }

// bot - at startup
val result = botConfig.toWikiConfig() // everything available is enabled
    .flatMap { wikiConfig -> configureWikiUseCase(wikiConfig) }
```

### Config
- use cases called before configuration wait for the config instead of failing (`filterNotNull().first()`)
- any service that needs available / enabled games loads them through `LoadWikiConfigPort`

### Enabling / disabling a wiki
- app flow
  1. the user flips a game in settings
  2. the app saves the choice to DataStore
  3. only if the save succeeded - the app calls `ConfigureWikiUseCase(newConfig)`
  4. the app wipes the disabled game's media (`MediaRepo` stays in the app)
- the wiki reacts
  - enabled game - start a refresh in the module's scope, so it survives navigation
- two stores (DataStore in the app, frame data in the wiki SQL DB) - no shared transaction
  - the same tradeoff the app has today - carry it over unchanged
  - invariant: enabled + corrupt/partial data is unacceptable; disabled + re-download is acceptable

### Starting the module
- no `initialize()` - the module is ready once Koin can resolve it
- host setup
  1. load `wikiModule()`
  2. read + map its own config, call `ConfigureWikiUseCase(wikiConfig)`
  3. call use cases
- constructors do no I/O
- proactive work (periodic refresh, launch-time sync) is an explicit use case
  - the host decides *when* (bot - schedule, app - launch)
  - the module decides *what*

### Statefulness
- like a stateless BE - request in, read/write stores through ports, result out
- stores
  - SQL DB - frame data
  - in-memory adapter - running config; rebuildable, the host's sources (JSON, DataStore) are the source of truth
- short-lived coordination in memory - refresh in progress, so concurrent callers share one download
- if memory ever holds something that exists nowhere else, the design has gone wrong
- `Flow`-returning use cases stay stateless - SqlDelight's query `Flow` does the watching

### Findings
- naming
  - `port/inbound` - only `*UseCase`; the outside calls them
  - `port/outbound` - `*Port`; the wiki's services call them, an adapter does the work
  - a port is not a use case - naming it `*UseCase` would suggest the outside may call it
  - the port names the capability, the method names the mode - `LoadXPort.subscribe()` / `get()`, `SaveXPort.save()`, `DeleteXPort.delete()`, `FetchXPort.fetch()`
- errors
  - the outside only ever sees `WikiError` - every use case returns it
  - ports may return `DataError`; services map it with `toWikiError()`
- inbound adapters live in the hosts (bot's Discord feature, app's VM)
  - the wiki has no protocol of its own (HTTP, Discord, UI), so there is nothing to translate - no `adapter/inbound`
- a port the host implements is a runtime risk - a missing Koin binding crashes on first use, not at compile time
  - that's why the config comes in through a use case, not a host-implemented port
- config is a dependency of the services, not an input of each use case call
  - use case parameters = what the caller wants, differs per call (`characterQuery`, `moveQuery`, ...)
  - config / DB / HTTP client = fixed for the whole run - never part of a use case signature
