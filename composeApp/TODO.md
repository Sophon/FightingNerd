# APPS TODO

## High prio

## Low prio

- first time launch tutorial dialog
  - can tell the user about Swipe-to-refresh
- Home - character search
- Favorites - character bookmarking
- older games
  - Wavu 
    - T7
  - DustLoop
    - GG Xrd
    - DNF Duel
  - SuperCombo
    - [UMvC3](https://wiki.supercombo.gg/w/Ultimate_Marvel_vs_Capcom_3)
  - Mizuumi
    - Pokemon CC
    - SamSho
  - [Injustice 2](https://gist.github.com/taozenforce/401947902eaa1dd343bac4beb33f2a6a)

## Web platform

- `fightingnerd.app/` — static HTML/CSS landing page (store badges, Discord, donations)
- `fightingnerd.app/web` — CMP WASM app

### Website repo

Deployment target for `fightingnerd.app`, symmetric with Fly.io / Play Store / App Store.

**Structure:**

- `/index.html`, `/styles.css`, `/assets/` — landing page (manual commits)
- `/web/` — CMP WASM build (auto-pushed from main repo CI, never hand-edited)

**Branches:**

- `main` — live site, Pages deploys on push
- `staging` — receives WASM builds from `appRelease`, merged to `main` when ready

**Workflow:**

- `deploy-pages.yml` — fires on any push to `main`, uploads repo as Pages artifact

**Credential:**

- `WEBSITE_REPO_TOKEN` — PAT on main repo, scoped to website repo contents

### Rollout

1. Static landing page
2. Empty wasmJs target builds and deploys
3. One game, one screen, hardcoded data
4. SQLDelight web worker + Ktor wired up
5. Settings, routing, feature parity

## Ideas
