package io.github.sophon.fightingnerd.feat.move.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.CoreFilters
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.util.filterMatching
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MIN_STARTUP
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveFiltersUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveGroupsUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.NormalizeSliderUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.SubscribeToMoveListUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MoveListVM(
    private val gameId: String,
    private val characterId: String,

    private val overlayService: OverlayService,
    private val subscribeToMoveListUseCase: SubscribeToMoveListUseCase,
    private val loadMoveFiltersUseCase: LoadMoveFiltersUseCase,
    private val loadMoveGroupsUseCase: LoadMoveGroupsUseCase,
    private val normalizeSliderUseCase: NormalizeSliderUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(MoveListState())
    private val _fullMoveList = MutableStateFlow(MoveCache.EMPTY)

    val state = _state
        .onStart {
            subscribeToData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MoveListState(),
        )

    val filteredMoves: StateFlow<ImmutableList<UiMove>> = combine(
        _state.distinctUntilChanged { old, new ->
            old.searchQuery == new.searchQuery
                    && old.filterSheet.activeFilterSet == new.filterSheet.activeFilterSet
                    && old.filterSheet.startup == new.filterSheet.startup
                    && old.filterSheet.onHit == new.filterSheet.onHit
                    && old.filterSheet.onBlock == new.filterSheet.onBlock
        },
        _fullMoveList,
    ) { state, cache ->
        val filters = state.filterSheet.activeFilterSet + state.filterSheet.buildSliderFilters()
        val filtered = cache.applyFilters(
            filterSet = filters,
            searchQuery = state.searchQuery,
        )
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf(),
    )


    init {
        loadMoveFiltersFor(gameId)
        loadMoveGroupsFor(gameId)
    }


    fun onSearchInput(searchQuery: String?) {
        _state.update { it.copy(searchQuery = searchQuery) }
    }

    fun onDisplayFilter(isVisible: Boolean) {
        _state.update { it.copy(filterSheet = it.filterSheet.copy(isVisible = isVisible)) }
    }

    fun onClearFilters() {
        _state.update { state ->
            val resetFilterSheet = state.filterSheet.copy(
                activeFilterSet = persistentSetOf(),
                startup = null,
                onBlock = null,
                onHit = null,
            )
            state.copy(filterSheet = resetFilterSheet)
        }
    }

    fun toggleFilter(filter: Filter) {
        _state.update { state ->
            val current = state.filterSheet.activeFilterSet
            val newFilterSet = if (filter in current) {
                (current - filter).toImmutableSet()
            } else {
                (current + filter).toImmutableSet()
            }
            state.copy(filterSheet = state.filterSheet.copy(activeFilterSet = newFilterSet))
        }
    }

    fun onChangeStartup(minMax: MoveListState.FilterSheet.MinMax?) {
        _state.update { state ->
            val normalized = normalizeSliderUseCase.invoke(
                newMinMax = minMax,
                sliderMin = FRAME_MIN_STARTUP,
            )
            state.copy(filterSheet = state.filterSheet.copy(startup = normalized))
        }
    }

    fun onChangeOnHit(minMax: MoveListState.FilterSheet.MinMax?) {
        _state.update { state ->
            val normalized = normalizeSliderUseCase.invoke(newMinMax = minMax)
            state.copy(filterSheet = state.filterSheet.copy(onHit = normalized))
        }
    }

    fun onChangeOnBlock(minMax: MoveListState.FilterSheet.MinMax?) {
        _state.update { state ->
            val normalized = normalizeSliderUseCase.invoke(newMinMax = minMax)
            state.copy(filterSheet = state.filterSheet.copy(onBlock = normalized))
        }
    }

    fun onMoveClick(moveId: String) {
        _state.update { state ->
            val newExpandedMoveId = if (state.expandedMoveId == moveId) null else moveId
            state.copy(expandedMoveId = newExpandedMoveId)
        }
    }


    private fun subscribeToData() {
        viewModelScope.launch {
            subscribeToMoveListUseCase.invoke(gameId = gameId, characterId = CharacterId(characterId))
                .collectLatest { result ->
                    result
                        .onSuccess { (character, moveList) ->
                            val movesById = moveList.associateBy { it.id }.toImmutableMap()
                            val uiMovesById = moveList
                                .associate { move ->
                                    val uiMove = move.toUiMove()
                                    move.id to uiMove
                                }
                                .toImmutableMap()
                            _fullMoveList.value = MoveCache(
                                movesById = movesById,
                                uiMovesById = uiMovesById,
                            )
                            _state.update { state ->
                                state.copy(
                                    character = MoveListState.MoveListCharacter(
                                        displayName = character.displayName,
                                    ),
                                )
                            }
                        }
                        .onError { error ->
                            Napier.e(tag = TAG) { "loadData: $error" }
                            overlayService.show(error)
                        }
                }
        }
    }

    private fun loadMoveFiltersFor(gameId: String) {
        loadMoveFiltersUseCase.invoke(gameId)
            .onSuccess { filterSet ->
                val immutableFilterSet = filterSet.toImmutableSet()
                _state.update {
                    it.copy(filterSheet = it.filterSheet.copy(filterSet = immutableFilterSet))
                }
            }
            .onError { error ->
                Napier.e(tag = TAG) { "loadMoveFiltersFor ($gameId): $error" }
                overlayService.show(error)
            }
    }

    private fun loadMoveGroupsFor(gameId: String) {
        loadMoveGroupsUseCase.invoke(gameId)
            .onSuccess { groupList ->
                _state.update { it.copy(groupList = groupList.toImmutableList()) }
            }
    }

    private fun MoveListState.FilterSheet.buildSliderFilters(): List<Filter> {
        val list = listOfNotNull(
            startup?.let { CoreFilters.Startup(it.min, it.max) },
            onHit?.let { CoreFilters.OnHit(it.min, it.max) },
            onBlock?.let { CoreFilters.OnBlock(it.min, it.max) },
        )
        return list
    }

    private fun MoveCache.applyFilters(
        filterSet: Set<Filter>,
        searchQuery: String?,
    ): ImmutableList<UiMove> {
        val filtered = movesById.values
            .filter { move ->
                filterSet.all { it.predicate(move) }
            }
            .filterMatching(searchQuery)
            .mapNotNull { move -> uiMovesById[move.id] }

        val result = filtered.toImmutableList()
        return result
    }


    private data class MoveCache(
        val movesById: ImmutableMap<String, Move>,
        val uiMovesById: ImmutableMap<String, UiMove>,
    ) {
        companion object {
            val EMPTY = MoveCache(persistentMapOf(), persistentMapOf())
        }
    }


    private companion object {
        const val TAG = "MoveListVM"
    }
}
