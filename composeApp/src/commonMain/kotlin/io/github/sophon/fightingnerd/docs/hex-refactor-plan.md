# HEXDDD REFACTOR PLAN

Top priority - HexDDD, aligning to our `ARCHITECTURE.md`.

Secondary priority - have the packages ready for the future AGP 9 refactor.

## Adapter

### Inbound

- should be screens
  - so the classic VM, state and screen

### Outbound

- `dataStore` for preferences
  - in the future, with WASM, we will remove this package
  - we will only have `PreferencePort` and then `mobileMain/../DataStoreAdapter` and `wasmJsMain/../LocalStorageAdapter`
- `wiki` module dependency
- `ktor` for remote calls - GitHub releases for changelog
- `revenueCat` for tips
- `compose` for compose resources - `modules.json` feature config

## App

### InPort

- the vast majority of our old usecases become interfaces here

### Service

- the vast majority of our old usecases become services that implement `inPort`

