package io.github.sophon.fightingnerd.feat.move.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MAX
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MIN_STARTUP
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.toUiMove
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveFiltersUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveListDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MoveListVM(
    private val gameId: String,
    private val characterId: String,

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


    init {
        loadMoveFiltersFor(gameId)
    }


    fun onDisplayFilter(isVisible: Boolean) {
        _state.update { it.copy(filterSheet = it.filterSheet.copy(isVisible = isVisible)) }
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


    private fun loadData() {
        viewModelScope.launch {
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
                .onError { Napier.e(tag = TAG) { "loadData: $it" } }
        }
    }

    private fun loadMoveFiltersFor(gameId: String) {
        loadMoveFiltersUseCase.invoke(gameId)
            .onSuccess { filterSet ->
                val filterSheet = state.value.filterSheet.copy(filterSet = filterSet)
                _state.update { it.copy(filterSheet = filterSheet) }
            }
            .onError {
                //TODO: error toast
                Napier.e(tag = TAG) { "loadMoveFiltersFor ($gameId): $it" }
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
        return if (newMin == null && newMax == null) {
            null
        } else {
            copy(min = newMin, max = newMax)
        }
    }


    private companion object {
        const val TAG = "MoveListVM"
    }
}

internal fun Collection<Move>.applyFilters(filterSet: Set<Filter>): List<MoveListState.UiMove> {
    val filtered = this
        .filter { move ->
            filterSet.all { it.predicate(move) }
        }
        .map { it.toUiMove() }

    return filtered
}
