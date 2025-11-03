package io.github.sophon.cornerman.screens.moveList.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.cornerman.screens.moveList.domain.MoveCategory
import io.github.sophon.cornerman.screens.moveList.domain.usecase.FetchMoveListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MoveListVM(
    private val charName: String,
    private val fetchMoveListUseCase: FetchMoveListUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(MoveListViewState())
    val state = _state
        .onStart {
            Napier.d(tag = TAG) { "bingo: $charName" }
            fetchMoves()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MoveListViewState()
        )


    fun onExpandNotesFor(moveId: String) {
        _state.update {
            it.copy(
                expandedNotesId = if (moveId in it.expandedNotesId) {
                    it.expandedNotesId - moveId
                } else {
                    it.expandedNotesId + moveId
                }
            )
        }
    }

    fun onStartSearch() {
        _state.update { it.copy(searchBar = MoveListViewState.SearchBar(type = MoveListViewState.SearchBar.Type.FIELD)) }
    }

    fun onSearch(query: String) {
        Napier.d(tag = TAG) { "Searching: $query" }
        _state.update {
            it.copy(
                searchBar = MoveListViewState.SearchBar(
                    query = query,
                    type = MoveListViewState.SearchBar.Type.FIELD
                ),
                filteredMoves = filterMoves(query),
            )
        }
    }

    fun onSearchDone() {
        Napier.d(tag = TAG) { "Search done" }
        _state.update {
            it.copy(
                searchBar = MoveListViewState.SearchBar(
                    query = it.searchBar?.query.orEmpty(),
                    type = MoveListViewState.SearchBar.Type.CHIP
                )
            )
        }
    }

    fun onClearSearch() {
        Napier.d(tag = TAG) { "Clear search" }
        _state.update { it.copy(searchBar = null, filteredMoves = it.allMoves) }
    }


    private suspend fun fetchMoves() {
        when (val result = fetchMoveListUseCase.invoke(charName)) {
            is Result.Success -> {
                _state.update { it.copy(allMoves = result.data, filteredMoves = result.data) }
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
                _state.update { it.copy(error = result.error.toString()) }
            }
        }

        _state.update { it.copy(isLoading = false) }
    }

    private fun filterMoves(query: String): List<MoveCategory> {
        return _state.value.allMoves.mapNotNull { moveCategory ->
            val filteredMoves = moveCategory.moves.filter { move ->
                move.id.contains(query, ignoreCase = true)
                        || move.notes.any { it.contains(query, ignoreCase = true) }
            }

            if (filteredMoves.isEmpty()) null else moveCategory.copy(moves = filteredMoves)
        }
    }
}

private const val TAG = "MoveListVM"