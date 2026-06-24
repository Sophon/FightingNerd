package io.github.sophon.fightingnerd.feat.move.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MAX
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MIN_STARTUP
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.toUiMove
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveFiltersUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveListDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

internal class MoveListVM(
    private val gameId: String,
    private val characterId: String,

    private val overlayService: OverlayService,
    private val loadMoveListDataUseCase: LoadMoveListDataUseCase,
    private val loadMoveFiltersUseCase: LoadMoveFiltersUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(MoveListState(character = null))
    val state = _state
        .onStart {
            loadData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MoveListState(null),
        )
    val filteredMoves: StateFlow<List<UiMove>> = _state
        .map { state ->
            val filters = state.filterSheet.activeFilterSet + state.filterSheet.activeSliderFilters
            state.fullMoveList.values.applyFilters(
                filterSet = filters,
                searchQuery = state.searchQuery,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )


    init {
        loadMoveFiltersFor(gameId)
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
                activeFilterSet = emptySet(),
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
                current - filter
            } else {
                current + filter
            }
            state.copy(filterSheet = state.filterSheet.copy(activeFilterSet = newFilterSet))
        }
    }

    fun onChangeStartup(minMax: MoveListState.FilterSheet.MinMax?) {
        _state.update { state ->
            val normalized = minMax.normalize(min = FRAME_MIN_STARTUP, max = FRAME_MAX)
            state.copy(filterSheet = state.filterSheet.copy(startup = normalized))
        }
    }

    fun onChangeOnHit(minMax: MoveListState.FilterSheet.MinMax?) {
        _state.update { state ->
            state.copy(filterSheet = state.filterSheet.copy(onHit = minMax))
        }
    }

    fun onChangeOnBlock(minMax: MoveListState.FilterSheet.MinMax?) {
        _state.update { state ->
            state.copy(filterSheet = state.filterSheet.copy(onBlock = minMax))
        }
    }

    fun onMoveClick(moveId: String) {
        _state.update { state ->
            val newExpandedMoveId = if (state.expandedMoveId == moveId) null else moveId
            state.copy(expandedMoveId = newExpandedMoveId)
        }
    }


    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            loadMoveListDataUseCase.invoke(gameId = gameId, characterId = characterId)
                .onSuccess { (character, moveList) ->
                    _state.update { state ->
                        val fullMoveList = moveList.associateBy { it.id }
                        state.copy(
                            character = character,
                            fullMoveList = fullMoveList,
                        )
                    }
                }
                .onError { error ->
                    Napier.e(tag = TAG) { "loadData: $error" }
                    overlayService.show(error)
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadMoveFiltersFor(gameId: String) {
        loadMoveFiltersUseCase.invoke(gameId)
            .onSuccess { filterSet ->
                val filterSheet = state.value.filterSheet.copy(filterSet = filterSet)
                _state.update { it.copy(filterSheet = filterSheet) }
            }
            .onError { error ->
                Napier.e(tag = TAG) { "loadMoveFiltersFor ($gameId): $error" }
                overlayService.show(error)
            }
    }

    private fun MoveListState.FilterSheet.MinMax?.normalize(
        min: Int,
        max: Int,
    ): MoveListState.FilterSheet.MinMax? {
        if (this == null) return null
        if (isValid.not()) return null
        val sliderMin = min - 1
        val sliderMax = max + 1
        val newMin = if (this.min != null && this.min <= sliderMin) null else this.min
        val newMax = if (this.max != null && this.max >= sliderMax) null else this.max
        val normalized = if (newMin == null && newMax == null) {
            null
        } else {
            copy(min = newMin, max = newMax)
        }
        return normalized
    }

    private fun Collection<Move>.applyFilters(
        filterSet: Set<Filter>,
        searchQuery: String?,
    ): List<UiMove> {
        val filtered = this
            .filter { move ->
                filterSet.all { it.predicate(move) }
            }
            .filter { move ->
                searchQuery?.let { query ->
                    move.input.contains(query, ignoreCase = true)
                            || move.aliases.any { it.contains(query, ignoreCase = true) }
                            || (move.name?.contains(query, ignoreCase = true) == true)
                } ?: true
            }
            .map { move ->
                val uiMove = move.toUiMove()
                uiMove
            }

        return filtered
    }


    private companion object {
        const val TAG = "MoveListVM"
    }
}
