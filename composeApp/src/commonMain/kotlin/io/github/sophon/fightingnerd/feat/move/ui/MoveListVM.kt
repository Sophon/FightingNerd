package io.github.sophon.fightingnerd.feat.move.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.util.filterMatching
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MIN_STARTUP
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveFiltersUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.NormalizeSliderUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.SubscribeToMoveListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
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
    private val normalizeSliderUseCase: NormalizeSliderUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(MoveListState(character = null))
    val state = _state
        .onStart {
            subscribeToData()
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
                }
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

    private fun Collection<Move>.applyFilters(
        filterSet: Set<Filter>,
        searchQuery: String?,
    ): List<UiMove> {
        val filtered = this
            .filter { move ->
                filterSet.all { it.predicate(move) }
            }
            .filterMatching(searchQuery)
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
