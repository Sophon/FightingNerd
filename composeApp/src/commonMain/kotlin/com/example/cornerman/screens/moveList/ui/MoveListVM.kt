package com.example.cornerman.screens.moveList.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.Result
import com.example.cornerman.screens.moveList.domain.FetchMoveListUseCase
import com.example.cornerman.screens.moveList.domain.MoveCategory
import com.example.wikiwavu.domain.model.Character
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MoveListVM(
//    character: Character,
    private val fetchMoveListUseCase: FetchMoveListUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(MoveListViewState())
    val state = _state
        .onStart {
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
        val character = mockCharacter()
        val result = fetchMoveListUseCase.invoke(character)
        when (result) {
            is Result.Success -> {
                cacheMoves(result.data)
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
            }
        }
        _state.update { it.copy(isLoading = false) }
    }

    private fun mockCharacter(): Character {
        return Character(
            name = "Dragunov",
            alias = listOf("drag"),
            portraitUrl = "https://i.imgur.com/MZClYKp.png",
            wavuPageUrl =  "https://wavu.wiki/t/Dragunov",
        )
    }

    //TODO: refactor to Database
    private fun cacheMoves(categories: List<MoveCategory>) {
        _state.update { it.copy(allMoves = categories, filteredMoves = categories) }
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