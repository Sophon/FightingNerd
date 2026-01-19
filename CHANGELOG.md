# BOT CHANGELOG

## [v8.4.2] - 2026-01-20
- `join` 
  - no longer displays the password field without password
  - uses username instead of servername

## [v8.4.1] - 2026-01-19
- HOTFIX Discord commands

## [v8.4.0] - 2026-01-19
- `join` command

## [v8.3.0] - 2026-01-18
- error embeds
  - shortened
  - added the game for UnknownMove

## [v8.2.0] - 2026-01-17
- `examples` command
- added buttons to error embeds
- DustLoop - fixed aliases for BB

## [v8.1.1] - 2026-01-15
- add missing GBVSR commands - `fdgb` and `chargb`

## [v8.1.0] - 2026-01-15
- DustLoop 
  - `aliasgg` command
  - fixed Nagoriyuki aliases - `2h` for *2h (level 1)*, `2h3` for *2h (level 3)* and `5hb` for *5h (Blood Rage)*

## [v8.0.0] - 2026-01-14
- `inv` invincible commands for 
  - MB, Uni, VSAV
- DustLoop
  - fixed incorrect move URLs
- Mizuumi - more VSAV aliases
- Wavu 
  - display the startup of the whole string
  - accepts starting heat for heat extensions ie `ak db2h.1` and `h.db21`
- no longer clogs up the logs with reconnection exceptions
- some buttons now stay permanently

## [v7.0.1] - 2026-01-11
- fixed `fdvs` command

## [v7.0.0] - 2026-01-11
- Mizuumi
  - VSAV support
  - fixed URL pointing to the wiki instead of game subsection
  - `charuni` command
  - UNI2 move embeds feature the char image
- Wavu
  - accepts all forms of `cd` type inputs, aliases and alts I could think of
  - fixed missing moves with the Heat property
- bot now ignores replies to its messages
- Details button lasts 15s, move-list buttons last 30s
- split `help` into `help` and `commands`
  - `help` also has a button that summons `commands`

## [v6.1.0] - 2026-01-09
- Mizuumi
  - Uni2 support
- fixed Full sometimes not showing hitbox images

## [v6.0.1] - 2026-01-09
- time-limit the SF detail button to 10s

## [v6.0.0] - 2026-01-08
- SuperCombo - SF6 outputs have a button to display details

## [v5.3.0] - 2026-01-08
- only hitbox images - remove all other images from move embeds

## [v5.2.3] - 2026-01-08
- Wavu - fixed Video field being inlined
- DustLoop - fixed move names having HTML mess

## [v5.2.2] - 2026-01-08
- fixed Markdown formatting

## [v5.2.1] - 2026-01-08
- DustLoop
  - fixed Susano'o name formatting
  - removed redundant nicknames for BB
- fixed Markdown bug with asterisks in fields

## [v5.2.0] - 2026-01-08
- Xko
  - supports non tilde variants ie `5L~L` and `5LL`
  - supports inputs without parentheses ie `j.M+H` is valid for `j.(M+H)`
  - uppercase input in the embed

## [v5.1.3] - 2026-01-07
- DustLoop
  - fixed ABA JR queries
  - slash variant support: `236s/h` creates aliases `236s` and `236h`
- log Discord embed errors
- Xko 
  - fixed some properties being nullable
  - supports slash variants

## [v5.1.2] - 2026-01-07
- Mizuumi - fixed clickable properties
- fixed embeds with truncated text

## [v5.1.1] - 2026-01-07
- DustLoop 
  - Invulnerable and Smash fields
  - fixed BBCF Susano'o

## [v5.1.0] - 2026-01-07
- DustLoop 
  - BBCF support
  - removed spaces from `aliasdb`
- improved syntax-error embed
- Mizuumi - fixed aliases
- changed buttons to the Primary style
- fixed `alias` commands not having color
- fixed spaces (leading, trailing, middle) for queries

## [v5.0.3] - 2026-01-06
- Mizuumi
  - fixed a bug with character names/aliases
  - fixed hitbox image issues not merging

## [v5.0.2] - 2026-01-06
- fixed a bug with wrong move aliases

## [v5.0.1] - 2026-01-05
- disabled `@bot command query` syntax until verification

## [v5.0.0] - 2026-01-05
- Wavu
  - `stance`, `pc`, `homing` and `heat` output embeds have button selectors

## [v4.0.1] - 2026-01-04
- added **1%** chance for embeds to trigger donation

## [v4.0.0] - 2026-01-04
- Mizuumi wiki
  - support for MBTL with `alias` and `fd`
- removed titles from Notes and Details

## [v3.0.6] - 2026-01-03
- better Kord logs for disconnects and reconnects

## [v3.0.5] - 2026-01-02
- syntax error embed mentions move name queries

## [v3.0.4] - 2025-01-02
- included `gameId` for logs

## [v3.0.3] - 2025-01-02
- more logging tweaks
- fixed duplicate config loading

## [v3.0.2] - 2025-01-02
- only log warnings and errors
- improved syntax error reply message
- fixed some input parsing

## [v3.0.1] - 2025-01-02
- better logging

## [v3.0.0] - 2025-01-02
- added footers to all wiki Discord embeds
- invalid queries include syntax embeds
- `alias` variants for KOF and COTW

## [v2.1.1] - 2025-12-31
- Wavu v1.0.8 - added TekkenDocs mention

## [v2.1.0] - 2025-12-31
- `aliasdb` and `aliastk` commands
- Discord bot - updated the status
- `feedback` - 20% chance donation message is included

## [v2.0.3] - 2025-12-24
- fixed infil glossary image layout

## [v2.0.2] - 2025-12-24
- Infil v1.0.2
  - fixed more formatting bugs
  - displays gifs and images
- admin - improved formatting
- SuperCombo - made embed more compact
- Wavu
  - more emojified notes
  - improved formatting for move-list based embeds
  - fixed command descriptions
- all queries have embeds with colors corresponding to their Wiki

## [v2.0.1] - 2025-12-22
- admin commands now hidden from `help` and the menu
- added missing footer to move-list based commands

## [v2.0.0] - 2025-12-22
- admin tools
  - allows giving feedback with `/feedback`
  - admins can `reply` to a feedback
  - `ban` and `unban` users from using `feedback`
  - admin tools and feedback channel configurable via `config.json`
- bugfixes
  - Wavu - html bugs

## [v1.2.7] - 2025-12-16
- Wavu
  - fixed some clickable links not formatting properly

## [v1.2.6] - 2025-12-16
- remove game-version from commands
- `help` has character-data commands split into a separate column
- DustLoop v1.0.5
  - enable DBFZ
  - `alias` command to see character querryable aliases
- Glossary v1.0.2
  - fixed formatting for `\n`/`<br>`

## [v1.2.5] - 2025-12-14
- stances display the character name
- stances include BT and CD

## [v1.2.4] - 2025-12-13
- fix stance's second argument being mandatory

## [v1.2.3] - 2025-12-13
- fix bugs related to searching via move's name
- Wavu v1.0.6
  - can search for a character's stances
  - can search for a stance's moves

## [v1.2.2] - 2025-12-12
- can search moves via its name
- Wavu v1.0.5
  - fixed some alias formats not forming

## [v1.2.1] - 2025-12-12
- display move images if hitbox images not available
- Wavu v1.0.4
  - supports `qcf`

## [v1.2.0] - 2025-12-11
- can search moves via its alias (ie `ak giant swing`)
- SuperCombo and DustLoop embeds display character image
- DustLoop v1.0.3
  - GBVSR support
  - DBFZ is implemented but disabled due to aliases
  - Level property

## [v1.1.2] - 2025-12-10
- fixed SuperCombo games not showing hitbox
- Wavu embed displays character image
- DustLoop v1.0.2
  - fixed formatting issues
- SuperCombo v1.0.3
  - MK1 support

## [v1.1.1] - 2025-12-09
- `charggst` also displays fastest normal
- core v1.1.1
  - sync with bot version
  - bot status
- DreamCancel v1.0.2
  - multiple hitbox images
- DustLoop v1.0.1
  - clickable Unique Movement Options
  - fixed note formatting
  - multiple hitbox images
- SuperCombo v1.0.2
  - clickable moves
- Wavu v1.0.3
  - clickable moves

## [v1.1.0] - 2025-12-08
- core v1.0.6
  - better logging on Kord disconnects
  - remove duplicates when fetching moves with properties
- Dustloop v1.0.0
  - Guilty Gear Strive support
- Infil glossary v1.0.1
  - fixed misformatted links

## [v1.0.10] - 2025-12-02
- core v1.0.5
  - better logging
- Wavu v1.0.2
  - formats the move query - allows queries like `rei df1,1`

## [v1.0.9] - 2025-11-28
- better formatting for `help`
- Tekken 8
  - notes with URLs don't truncate the rest of the note

## [v1.0.8] - 2025-11-27
- `help` command
- don't display clickable video unless valid link
- Wavu v1.0.1
  - Tekken 
    - stances don't require dots anymore
    - search moves via alias like JFSR

## [v1.0.7] - 2025-11-23
- display game in the thumbnails for DreamCancel wiki
- bugfixes
  - don't display move name if null

## [v1.0.6] - 2025-11-23
- hotfix for 2XKO

## [v1.0.5] - 2025-11-23
- DreamCancel:
  - COTW support
- bug fixes
  - multi input defaults to forward
    - `4/6ac > d` becomes `6ac >d`
  - input html decoding

## [v1.0.4] - 2025-11-22
- DreamCancel:
  - KoF 15 support
- expand field titles
- error embeds get deleted after ~6s
- updates every 3 hours
- bug fixes