package com.example.cornerman.moveList.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.Result
import com.example.cornerman.moveList.model.MoveCategory
import com.example.cornerman.moveList.useCase.FetchMoveListUseCase
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

    private suspend fun fetchMoves() {
        val character = mockCharacter()
        val result = fetchMoveListUseCase.invoke(character)
        when (result) {
            is Result.Success -> {
//                Napier.d(tag = TAG) { "Fetched ${result.data.moveList.size} moves for ${character.name}" }
                cacheMoves(result.data)
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
            }
        }
    }

    private fun mockCharacter(): Character {
        return Character(
            name = "Dragunov",
            alias = listOf("drag"),
            portraitUrl = "https://i.imgur.com/MZClYKp.png",
            wavuPageUrl =  "https://wavu.wiki/t/Dragunov",
        )
    }

    private fun cacheMoves(categories: List<MoveCategory>) {
        _state.update { it.copy(movesByCategory = categories) }
    }
}

private const val TAG = "MoveListVM"