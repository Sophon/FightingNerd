# BOT CHANGELOG

## [v16.4.0] - 2026-09-02
- autocomplete formatting

## [v16.3.0] - 2026-09-01
- streamline error embeds further

## [v16.2.1] - 2026-08-30
- streamline error embeds further

## [v16.2.0] - 2026-08-30
- error embeds reference slash commands

## [v16.1.0] - 2026-08-30
- autocomplete
  - fixed bugs
  - expand to more commands

## [v16.0.0] - 2026-08-30
- `Wavu` - stances are uppercase
- all commands are now global
  - removed game-specific `FD`, `Char`
  - removed `Inv`

## [v15.5.0] - 2026-08-28
- tagging attempts to extract command from query on error
- `Supercombo` - fixed Kimberly

## [v15.4.3] - 2026-08-24
- fixed promo embed showing on errors
- autocomplete for moves uses contains instead of startsWith

## [v15.4.2] - 2026-08-22
- `fly.io` has DB directory for persistent DB

## [v15.4.1] - 2026-08-22
- bot ignores tagged messages that don't start with the tag
- promote mobile apps

## [v15.4.0] - 2026-08-20
- `Wavu` - added Bob
- fixed search

## [v15.3.0] - 2026-08-18
- handle more Kord exceptions
- fixed slash commands displaying images
- set the memory logging from 3h to 2h

## [v15.2.0] - 2026-08-14
- JVM memory bump from 300 to 350MB
- reduce memory logging frequency (1 - 3 hours)
- fixed column formatting
- catch exceptions for button interactions

## [v15.1.0] - 2026-08-11
- `Wavu` - fixed move queries not being sanitized
- reduced memory settings for the Docker image and cloud setup

## [v15.0.0] - 2026-08-11
- periodic memory usage logging
- `help` - tweaked command output
- migrate to Wiki owned SQL databases

## [v14.3.3] - 2026-08-05
- `Dragdown` - updated character table
- `SuperCombo` - renamed field for `AVL` 

## [v14.3.2] - 2026-08-04
- fixed bug where the Embed would not render due to its button action being too long
  - Discord requires length 1-100

## [v14.3.1] - 2026-07-30
- video Embed response tags the interacting user

## [v14.3.0] - 2026-07-30
- `Wavu` - video Embed button

## [v14.2.1] - 2026-07-30
- fixed bug with partial OR inputs that have followups, ie `[4]6S/H~K`

## [v14.2.0] - 2026-07-25
- `Dragdown` - fixed broken URLs
- `DreamCancel` - character icons where possible
- `DustLoop` - add redirect feedback button
- `Mizuumi` - `MBTL` character icons where possible
- update Ko-Fi link

## [v14.1.0] - 2026-07-24
- broaden test coverage GitHub task to also check bot and app
- `fd` has global autocomplete for characters and moves
- `Discord`
  - cover usecases with tests
  - increased timeout window from 45s to 60s
- `Wavu` - remove `ed` from Eddy's nicknames
  - clash with SF Ed
- `DustLoop`
  - enable `MTFS` - Marvel Tokon: Fighting Spirit

## [14.0.0] - 2026-07-18
- move-list embeds use the Character's display name
- game specific `fd` - autocomplete
- `Mizuumi` - fixed mapper bugs
- memory DB stores move by ID, not by input

## [v13.1.0] - 2026-07-14
- `DragDown` - `specialsROA` command to list special moves
- `join` for steam lobbies does not require the command anymore
- fixed timeout on redirect button
- fixed character names in embeds

## [v13.0.0] - 2026-07-07
- set a timeout to wiki syncing
- `DustLoop` - fixed a bug with malformed URLs
- `DragDown`
  - enabled
  - supports Rivals of Aether 2

## [v12.1.7] - 2026-06-30
- `EWGF` - fixed the query being lowercased

## [v12.1.6] - 2026-06-29
- fixed bug where `stance` didn't search for character aliases

## [v12.1.5] - 2026-06-28
- `Discord` 
  - fixed bug that bundled disabled features
  - fixed `refresh`
- `Mizuumi`
  - fixed broken character Wiki URL
  - fixed bad ID causing `fd` to be unusable

## [v12.1.4] - 2026-06-27
- fixed slash commands requiring lowercase query

## [v12.1.3] - 2026-06-25
- `Wavu` - fixed move-list commands (`heat`, `pc` etc) requiring ID instead of alias

## [v12.1.2] - 2026-06-24
- `Wavu` - fixed the bot requiring non-clean input (ie `df1,2` instead of clean `df12`)

## [v12.1.1] - 2026-06-24
- hotfix queries no longer required to be lowercase

## [v12.1.0] - 2026-06-24
- fixed URLs for moves
- `core` - new architecture with `WikiClient` and `BaseWikiClient`
- refactor character parameter passing
- `DustLoop` - `GGST` ignores alt-mode characters
- `DreamCancel` - fixed bad character creation from data
- `Wavu` - fixed moves not having properly assigned high/low crush properties 
- add Top.gg link to donation message

## [v12.0.1] - 2026-06-06
- features are ordered in the repo
  - this allows game priority to be configured from the `config.json`

## [v12.0.0] - 2026-06-05
- architectural rework of feature loading
  - extract the config loading flow from DI to objects
- update donation links
- `wavu` - `hFC` moves have `FC` as alias

## [v11.7.2] - 2026-06-02
- `wavu` - fix input formatting for Heat-crouch-dash moves (ie AK's `H.cd1+2`)

## [v11.7.1] - 2026-05-28
- `ewgf` - `Region Not Set` region

## [v11.7.0] - 2026-05-28
- `admin` - `refresh` command to update data without restart 
- handle Kord REST exceptions

## [v11.6.0] - 2026-05-28
- refactor core's `Game`
  - a small step in a bigger refactor process that reworks how games and wikis are constructed per config
- `DustLoop` - update fields for `GBVSR`
- `Wavu` 
  - use custom character list json hosted on this repo
  - image links use Tekken Warehouse
- `Mizumi` 
  - `UNI` allows non-charge input for charge moves
    - ie `[4]6b` can be input via `46b`

## [v11.5.1] - 2026-05-02
- fixed bugs with wrong commands displaying command error embed
- increased the error auto edit duration to 20s
- hexagonal arch test and workflow

## [v11.5.0] - 2026-04-30
- `alias` command that outputs all game-specific alias commands
- better error output for invalid command
  - better description
  - `alias` button

## [v11.4.1] - 2026-04-23
- mention `feedback` in the footer

## [v11.3.1] - 2026-04-19
- `2xko` - fixed the Bucket missing Akali and Caitlyn

## [v11.3.0] - 2026-04-02
- `SuperCombo` - Avatar: Legends support
- `Wavu` - fixed alias for Matterhorn and such
- `GitHub`
  - two release modes:
    1. IMMEDIATE - same as before
    2. SCHEDULED - is released at 05:00 UTC; this is to start the release process AFTER daily report (which is at 00:00 UTC)

## [v11.2.0] - 2026-03-30
- `Wavu` - better emoji in Discord embeds
- normalization of inputs and aliases for all 2D games

## [v11.1.1] - 2026-03-28
- `SuperCombo`
  - accepts `st` notation
  - fixed issue with OR inputs (like Chun-Li's `4/6MP`)

## [v11.1.0] - 2026-03-27
- `SuperCombo`
  - add `cr` motion input support
  - fixed `360` inputs requiring `+`
- `DustLoop`
  - split move properties to basic and detailed
- `ewgf`
  - fixed slash command not showing the set list
  - `help` (alt input `?`) operation
  - `search` operation

## [v11.0.0] - 2026-03-25
- `stats` - show the total bot usage count
- `SuperCombo`
  - now supports motion inputs for SF
  - fixed Blanka's Rolling Cannon embed
- `DreamCancel` 
  - fixed issue multi-button ie
    - `236236b/d` now can be queried with both `236236b` or `236236d`
- guard Discord command registration and cleanup
- guard Kord embed creation and button interactions
- fixed `cl` input error

## [v10.5.0] - 2026-03-14
- `fd` - fixed not being able to search by move name or alias
- remove redundant EWGF command

## [v10.4.1] - 2026-03-13
- `EWGF` - fixed the game order within a set

## [v10.4.0] - 2026-03-13
- accept move inputs with spaces
- `EWGF`
  - shorter battle types
  - more columns
  - displays player rank

## [v10.3.0] - 2026-03-12
- `EWGF`
  - displays Type
  - fixed issue with GROUP battle type
  - clickable opponent
  - fixed the `-` command

## [v10.2.0] - 2026-03-12
- tag the person who pressed a button 
- fixed daily reports
- track failed interactions

## [v10.1.2] - 2026-03-12
- fixed case-sensitivity for frame-data features

## [v10.1.1] - 2026-03-12
- fix DB on fly.io

## [v10.1.0] - 2026-03-12
- `EWGF.gg` - feature enabled
  - `+`, `-`, `ewgf` and `update` commands
- fixed the period for daily reports

## [v10.0.0] - 2026-03-11
- handled some Kord exceptions
- `EWGF.gg` - prepare code for the final feature
- `stats` 
  - track command usage
  - daily reports
- DustLoop 
  - release notations have aliases without the brackets
  - updated fields for GBVSR
- admin - can redirect feedback to feature's feedback channel
- fixed character embeds not working

## [v9.1.1] - 2026-02-19
- Wavu
  - fixed bug with potentially infinite loop of parental moves

## [v9.1.0] - 2026-02-02
- fixed the HTTP client for Kord
- changed the editable embed window to 10s
- commands
  - reworked `help`
  - new: `modules`
- DustLoop - fixed GBVS and GGST aliases for alternative variants
- DreamCancel - fixed aliases for alternative variants
- Mizuumi - fixed aliases for alternative variants
- Wavu - heat smash (`h.2+3`) now has `heatsmash` and `hs` aliases

## [v9.0.0] - 2026-01-28
- error embeds edits itself to minimum after 5s
- made GGST and SF embeds more compact 
- fixed the `aliasCOTW` and `aliasKOF` commands

## [v8.8.4] - 2026-01-25
- fixed DSS not registering as stance

## [v8.8.3] - 2026-01-24
- `strings` also checks the move aliases

## [v8.8.2] - 2026-01-23
- increased cloud RAM to 1024

## [v8.8.1] - 2026-01-23
- increased JVM memory to 256

## [v8.8.0] - 2026-01-23
- `strings` 
  - fixed bug with multi-char starting move
  - now displays levels

## [v8.7.0] - 2026-01-23
- `join` - added optional lobby name parameter
- `string` command - searches followups of a move

## [v8.6.0] - 2026-01-22
- fixed DustLoop move notes URL formatting
- reworded the `fd` error syntax

## [v8.5.0] - 2026-01-21
- fixed `Lei-Lei` alias
- `throwtk` command
- chunked move-list embed

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