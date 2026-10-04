# BOT TODO

## High prio

- BUG: `char` UNI - Trait and Vorpal not formatted 
  - use formatLinks or something 
  - use `char Ogre`
- `character` and maybe other params - shorten
- `help` and `commands` - optional and mandatory parameters
- Mizuumi
  - Pokemon CC
  - SamSho

## Low prio

- `DragDown` - char embed should have the image as avatar, not main image
- `operator fun` for use case invokes
- from : to frame data function
  - `character: ; min: ; max: `
  - `inf` value
  - do it for startup, ob, oh, och
- local `*.db` files into `db/` instead of project root
  - change env fallbacks: ewgf, admin, wiki (`dcBotModule` x2, `"."` -> `"db"`)
  - ewgf + admin driver factories need `parentFile?.mkdirs()`
  - move existing local db files into `db/`
  - prod/fly unaffected - uses env vars

## Ideas

- How to point the user to the right command if they use the wrong one? 