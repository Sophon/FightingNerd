# FightingNerd

Frame data for fighting games, sourced from community wikis and served through the app and the Discord bot.

## Language

### Frame data

- **Alias** - An alternative name a Character or Move can be found by (`ak` for Armor King).
- **Character** - A playable fighter within one Game. The same fighter in two Games (Mai in KOF XV and COTW) is two Characters.
- **Game** - A fighting game title the project serves frame data for. Its data comes from exactly one Wiki.
- **Input** - The notation that performs a Move (`df1`, `5P`, `c.S`), unique within its Character.
- **Move** - One action of a Character, identified by its complete Input within that Character.
- **Wiki** - A community website that is the source of frame data for one or more Games (Wavu, DustLoop, SuperCombo).

### Bot

- **Character query** - The first word of a Query, naming a Character (`ak`).
- **Move query** - The rest of a Query after the Character query, naming a Move (`df1`).
- **Query** - The text a user sends the bot after the tag or command, such as `ak df1`.
