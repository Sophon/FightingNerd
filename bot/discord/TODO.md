# BOT TODO

## High prio

- `/fd` ignores game
  - steps: take a char that is in multiple games of one Wiki - like Gato in COTW and KOF
  - it will return first match
- BUG: `char` UNI - Trait and Vorpal not formatted 
  - use formatLinks or something 
  - use `char Ogre`
- `character` and maybe other params - shorten
- `help` and `commands` - optional and mandatory parameters
- Mizuumi
  - Pokemon CC
  - SamSho
- `games` command - displays all available games
  - `modules` only lists features + versions
- BUG: daily report not posted - "No archived report yet, nothing to post"
  - `ReportScheduler` fired at 23:59:59 UTC, before the stats day rollover, so `month.json` had nothing new
  - fixed `24.hours` period drifts; re-anchor each cycle with `delay(untilNextUtcMidnight() + grace)`
  - `WikiScheduler` and `GlossaryScheduler` fire early too, but harmlessly - they don't depend on the date
  - maybe also post only the report dated yesterday, and warn when it's missing
- Fly.io RAM: `768mb` → `512mb`
  - measured on prod: `VmHWM` 224MB, `VmRSS` 219MB, `free -m` used 247MB of 710MB
  - `fly.toml`: `memory = '512mb'` + `swap_size_mb = 256` as a cushion
  - `Dockerfile`: `-Xmx350m` → `-Xmx256m` - heap never committed above 198MB
  - before switching, check `VmHWM` again after uptime spans a full wiki refresh
    - `fly ssh console -C 'sh -c "grep -E \"VmRSS|VmHWM\" /proc/\$(pgrep -x java)/status"'`

## Low prio

- `DragDown` - char embed should have the image as avatar, not main image

## Ideas

- How to point the user to the right command if they use the wrong one? 