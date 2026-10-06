# ARCHITECTURE

This project uses a modified **Hexagonal + DDD architecture** (`hexDDD`), inspired by **Tom Homberg's Getting Your Hands Dirty With Clean Architecture** (`book`).

### Hexagonal architecture

Strict adherence, see [hexDDD.mmd](hexDDD.mmd).

### Packaging

Modified packaging - the priority was to have packages in a flat hierarchy, for faster navigation.

As such, there are differences from the book:

- `ports/in` moved to the root, renamed to `inPort`
- `app/ports/out` renamed and moved to `app/outPort`
- `app/domain` moved to `app/`
- `app/domain/model` split to public `model/` and internal `app/model`

```
<feature>/
├── inPort/                  (public)  - use case interfaces
├── model/                   (public)  - domain model exposed to the outside world
├── app/
│   ├── model/                         - internal domain model
│   ├── outPort/                       - ports implemented by outbound adapters
│   ├── service/                       - use case implementations
│   └── util/
├── adapter/
│   ├── inbound/                       - e.g. scheduler
│   └── outbound/                      - e.g. ktor, sqldelight, memory
└── docs/
```

### Testing

Architecture and packaging is enforced by Konsist.

Can be summoned via `./gradlew testHexagonal`.