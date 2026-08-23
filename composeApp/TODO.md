# Plan: Per-wiki move grouping + chapter navigation

## Context

Today's `MoveListScreen` renders a flat `LazyColumn` of moves in whatever order they arrive from SQLite (no `ORDER BY` in any `Move.sq`, no Kotlin-side sort). For app users this is unhelpful once a character has dozens of moves — there's no way to skim by category or jump to a section. The bot is unaffected: it looks up single moves and doesn't sort.

We want each wiki module to define a canonical grouping for its game (Tekken: Heat + starting-direction buckets; GGST: Normals / Universal / Specials / Supers) and the app to render that grouping with a floating **chapter button** in `MoveListScreen`: tap a chapter → scroll to the first move of that group.

The grouping API mirrors the existing `Filter` API (`core/wiki/model/Filter.kt` + `<X>Filters.kt` per wiki + `WikiClient.getFiltersFor(game)`), so the pattern is already familiar to the codebase.

## Design

### New `Group` type in core

`core/src/commonMain/kotlin/io/github/sophon/core/wiki/model/Group.kt`:

```kotlin
interface Group {
    val id: String
    val label: String
    val predicate: (Move) -> Boolean
}
```

A game's grouping is just `List<Group>` — the list order IS the chapter order. Assignment rule (implemented app-side): a move is assigned to the **first** `Group` whose `predicate` returns true. Moves that match nothing land in an app-appended `"Other"` group.

### Wiki contract

Add one method to `core/src/commonMain/kotlin/io/github/sophon/core/wiki/model/WikiClient.kt`:

```kotlin
fun getGroupingFor(game: Game): List<Group>? = null
```

Default implementation returns `null` (no grouping → flat list, chapter button hidden). Wikis opt in by overriding, following the existing `getFiltersFor` pattern (`require(game in supportedGameSet); when (game) { ... }`).

### Per-wiki `<X>Groupings.kt`

For each wiki that opts in, add `feat/wiki<Name>/src/commonMain/kotlin/io/github/sophon/wiki<name>/integration/model/<X>Groupings.kt` — same folder convention as the wiki's `<X>Filters.kt`. Each file exposes one `val <Game>Grouping: List<Group>`. Start with:

- **Wavu** (Tekken 8) — `TekkenGrouping: List<Group>` with Heat + starting-direction chapters (`n`, `f`, `df`, `d`, `db`, `b`, `ub`, `u`, `uf`). Reuse the input-parsing utilities used by `TekkenFilters.Strings` for the direction check.
- One GGST grouping (Normals / Universal Mechanics / Specials / Supers) in whichever wiki module exposes GGST — likely `wikiDustLoop` (`GGFilters` already lives there). Predicates draw on `move.ggstProperties` and existing helpers.

Other wikis (Supercombo, DreamCancel, Xko, DragDown, Mizuumi's other games) inherit the `null` default until someone designs their groupings.

### App layer

**Use case** — `composeApp/src/commonMain/kotlin/io/github/sophon/fightingnerd/feat/move/usecase/LoadMoveGroupingUseCase.kt`, sibling to `LoadMoveFiltersUseCase`:

```kotlin
fun invoke(gameId: String): Result<List<Group>?, AppError>
```

Same shape as `LoadMoveFiltersUseCase` — resolve `Game.fromId`, resolve `WikiClient` from `FeatureRepo`, delegate to `getGroupingFor(game)`.

**VM** — `composeApp/.../feat/move/ui/MoveListVM.kt`:

`filteredMoves` stays flat (`ImmutableList<UiMove>`). Add `List<Group>?` to state and a second derived flow `sectionIndex`. Both outputs are built in the same pass:

```kotlin
// MoveListState
val groups: List<Group>? = null

// New derived flow next to filteredMoves
val sectionIndex: StateFlow<ImmutableMap<String, Int>>

// Called at the tail of applyFilters, after filtering + mapping to UiMove
private fun regroup(
    filtered: List<UiMove>,
    groups: List<Group>?,
): Pair<ImmutableList<UiMove>, ImmutableMap<String, Int>> {
    if (groups == null) {
        val flat = filtered.toImmutableList()
        val emptyIndex = persistentMapOf<String, Int>()
        return flat to emptyIndex
    }

    val buckets = groups.associateWith { mutableListOf<UiMove>() }
    val other = mutableListOf<UiMove>()
    for (uiMove in filtered) {
        val group = groups.firstOrNull { it.predicate(uiMove.underlying) }
        if (group != null) buckets.getValue(group).add(uiMove) else other.add(uiMove)
    }

    val ordered = mutableListOf<UiMove>()
    val index = mutableMapOf<String, Int>()
    for (group in groups) {
        val bucket = buckets.getValue(group)
        if (bucket.isNotEmpty()) {
            index[group.id] = ordered.size   // start of this group in the flat list
            ordered += bucket
        }
    }
    ordered += other

    val result = ordered.toImmutableList() to index.toImmutableMap()
    return result
}
```

**Screen** — `composeApp/.../feat/move/ui/MoveListScreen.kt`:

`LazyColumn` and `MoveItem` are untouched — the list stays visually flat. Only the outer `Box` grows a `ChapterButton` overlay, hidden when there's no grouping.

```kotlin
Box(modifier) {
    MoveList(
        moveList = state.filteredMoves,
        listState = listState,
        onMoveClick = onMoveClick,
    )

    val groups = state.groups
    val sectionIndex by vm.sectionIndex.collectAsState()
    if (!groups.isNullOrEmpty()) {
        ChapterButton(
            groups = groups,
            onChapterClick = { groupId ->
                val idx = sectionIndex[groupId] ?: return@ChapterButton
                scope.launch { listState.animateScrollToItem(idx) }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(nerdDimensions.smallPadding),
        )
    }

    // existing FilterBottomSheet stays
}
```

Icon: a "book chapter" glyph — reuse `Icons.AutoMirrored.Filled.MenuBook` unless there's already a `nerdIcons` entry for this.

`ChapterButton` itself is a small collapsed FAB that expands into a vertical list of `group.label` rows on tap; collapse after a selection.

### Files to touch

Create:
- `core/.../wiki/model/Group.kt`
- `feat/wikiWavu/.../integration/model/WavuGroupings.kt`
- One GGST groupings file in the wiki that owns GGST
- `composeApp/.../feat/move/usecase/LoadMoveGroupingUseCase.kt`
- `composeApp/.../feat/move/ui/composables/ChapterButton.kt`

Modify:
- `core/.../wiki/model/WikiClient.kt` — add `getGroupingFor` with default `null`
- Wavu + GGST-owning `<X>WikiClient` — override `getGroupingFor`
- `composeApp/.../feat/move/ui/MoveListVM.kt` — hold `List<Group>?`, emit reordered `filteredMoves` + `sectionIndex`
- `composeApp/.../feat/move/ui/MoveListScreen.kt` — mount `ChapterButton`; list rendering unchanged
- `composeApp/.../fightingnerd/core/model/Module.kt` (Koin) — wire `LoadMoveGroupingUseCase` if the module uses explicit registration

Do not touch bot code, SQL, or `MoveRepo`.
