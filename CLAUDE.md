# FightingNerd

A KMP project for fighting game frame data. Targets Android, iOS and Discord - via a Discord bot.
All data is sourced from open-source resources like fighting game wikis.

## Structure
Two important target modules:
- `composeApp` - referred to as `app`
  - KMP + CMP stuff
  - targets Android, iOS; WASM in the future
- `bot` - referred to as `bot`
  - currently only has `discord` bot
  - targets JVM
- `Koin` dependency injection


Both platforms can use any of these:
- `core`
  - architecture stuff like `Result` including its extensions
  - HTTP networking via `Ktor`
  - feature configuration
    - each module inside `feat/` is a feature that target can use
    - contains `CoreFeatureRepo` which is the source of truth for all enabled features; the json files are:
      - bot: `~/res/config.json`
      - app: `~/composeApp/src/commonMain/composeResources/files/modules.json`
    - contains Wiki stuff
      - most feature modules are Wikis of fighting games - `DustLoop`, `SuperCombo`, `Wavu` etc
      - because most features are wikis, we have common Wiki architecture stuff, inside `wiki` and also `CoreWikiClientFactory`
  - utils
- `feat`
  - feature modules
  - most are Wiki based
  - bot uses most of them, app uses all the wiki ones
  - outside of Wiki ones, we also have
    - Infil Glossary - FGC terminology
    - EWGF - Tekken ELO stuff
    - admin - bot admin stuff like handling feedback, bans etc
    - stats - usage statistics

## Architecture
- MVVM + usecases + hexagonal
  - app obviously has VM, State and Screen
    - bot tries to have something similar - it has Discord Embed creation functions as pseudo-UI, usecases and DiscordFeature wrapper as pseudo-VM
  - most operations that don't involve manipulating the State or UI should be handled via usecases
    - even if all it does is calling DB/network/repo - we want the plug-n-play nature and testability
  - we generally want to avoid repo and lean towards usecases
    - in some cases, it's justified, like the feature repo or move repo - where we want to access the data from multiple places
    - all repo MUST work with the same data category - Move repo, Character repo, Feature repo etc
  - hexagonal is irrelevant for target modules, relevant for feature modules
    - everything inside a feature module must be internal or private
    - the only exception is anything inside `integration` which is our name for hexagonal's `plug`
      - this means that everything the outside world interacts with is inside `integration` and follows our packaging scheme
      - generally, the `integration` will have a client-like interface that will be the main point of interaction
      - the actual implementation will be inside the feature-module's `domain`
- we package by feature, not by layers
  - this means that we DO NOT have a big `data` and big `model`
  - this means we have `someFeature/data` or `someFeature/ui` etc
  - traditionally, `usecase` and `model` are inside `domain` but we have them flat - `someFeature/usecase` and `someFeature/model`
    - the reason is because we access `usecase` often, so having it flat is justified. The result is that having only `model` inside `domain` doesn't make sense
  - possible packages inside features:
    - `data` - data store, database, remote sources, dto, mappers
    - `usecase`
    - `model`
    - `ui` 
      - at the root, there should be VM, State and Screen
      - `components` for more composables
      - possible to have more screens inside `ui` - like `ui/list/ListVM` or `ui/detail/DetailState` etc
    - `native` - everything that requires platform implementation
      - we use `expect platformModule`
    - `util`
    - `doc` - documentation via Mermaid.js or MD files
- each module can have its own `core` or `util`; `constants` file and Koin `module` file 
- Wiki modules expect a database implementation

## App
KMP and CMP based. Currently targeting Android and iOS.
On Play Store and Apple Store.
- completely unified theme
  - `nerdTypography`, `nerdDimensions`, `nerdColorPalette`
  - we bind `nerdColorPalette` to Material 3 as close as possible as a fall-back solution; always prefer the `nerd-` stuff
- `navigation` - root `navigation` package 
  - `navigation3`
  - floating bottom bar that's only displayed for root destinations
  - root destinations can be identified in `Destination`'s `topLevelOrder`
- data 
  - `SQLDelight` + `DataStore<Preferences>`
  - enabling a feature starts data verifying data presence and then potentially download
  - disabling a feature calls Wiki feature's `clearCache()`
  - feature config requires confirm for that reason

## Bot
Kord based. Hosted on cloud via Fly.io (`fly.toml`)
- data 
  - Wiki - stored in memory via `Map<>`
  - EWGF 
    - has an SQL (SqlDelight) database of DiscordID-TekkenID pairings
    - user can register, unregister and check data
- each feature is bound via `DiscordRegisteredFeature` wrapper interface
  - each feature then has periodic data refresh; defined in Constants
  - each feature can have a default command and supported commands
  - a complete list of commands is in bot's `feat/core/domain/model/Command`
- two modes of interacting with the bot
1. tagging - `@bot (optional command) (queries)`
2. slash - `/command (queries)`
- command logic
  - search all features for a command that matches the Discord message/interaction
  - returns data on first match; the data can be Success or Error
  - slash command is explicit
  - for tag, the command is optional
    - if the first word following the tag is a command, use that command - same as slash
    - if the first word is not a command, use each feature's default command

## Infrastructure
- tests
  - all tests are inside root `build.gradle.kts`
  - all workflows for tests are inside `github/workflows/`
  - `unitTests` (`test-unit.yml`) 
    - runs all test classes
    - `./gradlew unitTests --rerun-tasks`
  - `testCoverage` (`test_coverage.yml`)
    - what must be covered with unit tests
      - mostly `core` utils, feature module usecases
    - `./gradlew testCoverage --rerun-tasks`
  - `hexagonal` (`test_arch_hex`)
    - checks for `internal` and that public can only be inside `integration`
    - `./gradlew testArchHexagonal --rerun-tasks`
- static analysis
  - done via detekt
    - `./gradlew detekt`
    - `github/workflows/detekt.yml`
- releases
  - we have release branches - `release_bot` or `release_app`
  - when there's a new release:
  1. switch to the release branch
  2. merge and squash from `dev`
  3. the commit will be the version number
  4. apply tag with the version number
  5. push to remote
  6. trigger a workflow for release - `deploy-bot-immediate` or `deploy-bot-scheduled`
  7. in other words, each commit in the release branch is a release
  - the steps above are handled by `scripts/botRelease.sh` or `scripts/appRelease.sh`
    - bot release takes version name (including the starting `v`) and then immediate or scheduled
    - immediate creates a release immediately
    - scheduled creates a release at 0200 UTC
- each merge requests requires
  - successful pass of all tests
  - successful detekt