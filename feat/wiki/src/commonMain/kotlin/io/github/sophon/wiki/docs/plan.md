# Wiki module

- hexagonal + DDD
  - inspired by Tom Homberg's Get Your Hands Dirty on Clean Architecture
- we should aspire to have a deep module, not a wide module

## Design
- do we need `adapter/in`? or should we simply call the `port/in`?
- `port/out`
  - ktor - REST requests from wikis
  - sql - store frame data of moves and characters
- SQL
  - one database for all wikis
  - `move` and `character` tables have invariants that are different per game
  - ID? with one DB instead of DB per module, we will prob need `move-char-game` as part of ID
  - char : move is `1:n`
  - while technically possible, char : game is **not** `n:n` - crossovers (like Mai in SF, KOF and COTW) are considered different chars
  

## Plan

1. design the interface - how the outside interacts with this module
2. move what can be moved from `core` - mostly wiki stuff
3. move a single-game wiki module (`wavu`) to `wiki`
    - a big refactor - single SQL database
4. move a multi-game wiki module (`dustloop`) to `wiki`
5. move the rest of the wikis
6. extensive testing

