# Hexagonal refactor

- everything is `internal` with the exceptions of
  - `domain/model`
  - `port`

```
project
│
├── wiki                       ← domain + adapters
│   ├── domain               ← Move, Character, Game, per-game properties
│   ├── port                  ← WikiFetchPort
│   ├── service               ← domain services
│   ├── db                    ← one shared DB (wiki-scoped tables)
│   └── adapter
│       ├── wavu
│       ├── dustloop
│       ├── supercombo
│       ├── xko
│       ├── dreamcancel
│       ├── mizuumi
│       └── dragdown
│
├── feat                       ← shared BC hexagons
│   ├── ewgf
│   └── glossary-infil
│
├── compose                   ← application
│   ├── commonMain
│   │   └── feat              ← compose-side BCs
│   │       ├── media
│   │       ├── quiz
│   │       ├── review
│   │       ├── payment
│   │       ├── changelog
│   │       └── updates
│   ├── androidMain           ← Android bootstrap
│   └── iosMain               ← iOS bootstrap
│
├── bot                        ← application
│   └── feat                  ← bot-side BCs + wiki wrappers
│       ├── wiki-wavu
│       ├── wiki-dustloop
│       ├── wiki-supercombo
│       ├── wiki-xko
│       ├── wiki-dreamcancel
│       ├── wiki-mizuumi
│       ├── wiki-dragdown
│       ├── ewgf
│       ├── glossary-infil
│       ├── admin
│       └── stats
│
└── core-shared              ← Result, Error, HttpClient, utils
```

### `:wiki` is one BC with multiple driven adapters

- One shared domain: `Move`, `Character`, `Game`, per-game property types
- One `WikiFetchPort` (out port); each wiki (Wavu, Dustloop, SuperCombo, XKO, DreamCancel, Mizuumi, DragDown) is a driven adapter implementing it
- Each adapter has its own invariants and URLs but feeds the same shared model
- Per-game property variations (Tekken stances, SF EX moves, GG Roman cancels) live in the shared domain as game-specific types, not in the adapters
- One shared wiki DB with wiki-scoped tables; the schema is yours, not the wikis'
- Adding a new wiki is a new adapter implementing the same port, plus its tables in the shared DB

### Shared BC hexagons live in `feat/`

- `feat/ewgf` and `feat/glossary-infil` are bounded contexts — each its own hexagon with in ports, out ports, domain, services
- Consumed by `:bot` today; `:compose` could consume them the same way
- Adding a new shared BC is a new hexagon under `feat/`, same shape

### `:compose` and `:bot` are DDD applications with multiple BCs

- Each is a decoupled BC in Hombergs' terms: an application whose hexagon contains multiple BCs inside it
- `:compose` contains its own BCs (`media`, `quiz`, `review`, `payment`, `changelog`, `updates`) plus consumes `:wiki` and shared `feat/` in ports
- `:bot` contains its own BCs (`admin`, `stats`) plus wiki/ewgf/glossary wrappers that adapt shared domains to Discord embeds and commands, plus consumes `:wiki` and shared `feat/` in ports
  - the wrapper pattern is subject to change
- Each application has its own DI graph, its own config, its own runtime — they don't share an orchestration layer

### The wiki wrappers inside `:bot/feat`

- Each `bot/feat/wiki-*` wraps `:wiki` for Discord: embed builders, command handlers, formatting specific to that wiki's data shape
- They depend on `:wiki`'s in port, not its internals
- Compose doesn't have wrappers because its consumption pattern is direct (VMs read from `:wiki` and format for UI)

### Dependency direction

- All dependencies point inward toward the domain
- Applications (`:compose`, `:bot`) depend on `:wiki` and shared `feat/` in ports
- Shared BCs don't depend on the applications that consume them, and don't depend on each other. Each BC is self-contained; cross-BC composition happens inside the applications.

### The in port is the only entry into a BC

- One door into each BC: the in port interface
- Everything that drives a BC — a ViewModel, a command handler, another BC's service — walks through that same door
- No framework reaches into a BC directly

### Shared domain, no mapping tax

- Domain models used across BCs are public and shared — for example `Move` and `Character`
- No duplicate representations of the same concept between BCs
- The "domain adapter" nightmare (n² mapping adapters between per-BC hexagons) is avoided by keeping shared concepts as one public type owned by one BC

### Public surface per BC

- In ports (use case interfaces)
- Out ports (interfaces the adapters implement)
- Domain models that appear in port signatures
- Everything else — services, out adapters, internal value objects — is `internal`

### Out adapters can be multiplied or swapped

- `:wiki` has seven real implementations of `WikiFetchPort`, one per wiki source
- Same shape applies elsewhere: swap real persistence for in-memory in tests, swap a Ktor HTTP adapter for a stub
- This is the payoff of routing every external call through an out port