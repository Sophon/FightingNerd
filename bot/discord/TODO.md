# BOT TODO

## High prio

- `media` command
  - separate command, same pattern as `fd` but returns media of a move
  - Video button uses `media` instead of raw URL - we avoid the 120 char limit of a button
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

## Low prio

- `DragDown` - char embed should have the image as avatar, not main image

## Ideas

- How to point the user to the right command if they use the wrong one? 