package com.example.cornerman.moveList.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MoveListVM: ViewModel() {
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
        //
    }
}