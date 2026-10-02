# TODO: Details button

## Overview
Move embeds have a Details button that expands the embed in place.
Currently, the message ID and the expanded embed are kept in an in-memory map and evicted after ~30s.
This scales badly, and the map is lost on restart/redeploy, so pending buttons die.

Goal: make the button stateless. It carries the move's exact ID in its `custom_id`; on click, we look the move up by that ID and edit the message with the expanded form.
Flow: `docs/flow-expand.mmd`

## Points
- The button carries `MoveId` (game, character `naturalId`, move `input`), not the user's query
  - matches the wiki's own keys - `UNIQUE (game, natural_id)` on character, `UNIQUE (character_id, input)` on move
  - no query parsing, no command, no router, no alias matching
- Exact ID lookup is wiki data logic, not bot logic
  - `feat/wiki` gets `GetCharacterUseCase(id)` and `GetMoveUseCase(characterId, input)`, backed by keyed SQL selects through new load ports
  - a missing row returns `WikiError.UnknownCharacter` / `WikiError.UnknownMove`
- `FrameDataPort` has a second method - `getFrameData(moveId)` - same port, different param; returns `MoveResponse`
- The wiki mapper builds all of a move's buttons into `MoveResponse.buttonSet` - the only place with `character.id` and `move.input` together
  - Details - `Action.Expand(moveId)`, only when the move is collapsed by default and has secondary fields
  - Video - `Action.Text(videoUrl)`
  - Kord only renders them - `DiscordButtonBuilder.createResponseButtons`
- `custom_id` format: `expand:<Game enum name>:<naturalId>:<input>` - `DiscordButton.Expand`, separator `DiscordButton.BUTTON_ID_DELIMITER`
  - `input` last, decoded with a split limit, so a `:` inside an input doesn't break decoding
  - well under Discord's 100-char limit
- The expanded form is an internal flag (`forceExpand`) on `MoveResponse`
  - slash and tag never set it - nothing for the user to type
  - only the button flow sets it, and drops the Details button from `buttonSet` at the same time
- Message ID comes from the interaction event; Kord keeps it and hands it to the poster - the application layer never sees it
- The click goes through its own use case - `ProcessButtonEventUseCase(ButtonEvent)`
  - Kord decodes `custom_id` into a `ButtonEvent` (`Expand(moveId)`, `Text(text)`); any other button is logged and ignored - the legacy handler and its in-memory message map are gone
  - Kord acknowledges first (Discord's 3s limit) - message update for Expand, deferred reply for Text
  - Expand - `FrameDataPort.getFrameData(moveId)`, `forceExpand = true`, Details dropped; Kord edits the message, `moveEmbed` picks the detailed embed
  - Text - `BotResponse.PlainText`; Kord replies with the clicker's mention and the text, so a video URL unfurls into a player
- Stale button - a refresh deleted or renamed the move - returns `UnknownMove`, never a different move
