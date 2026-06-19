package io.github.sophon.fightingnerd.feat.moveList.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.feat.moveList.ui.MoveListState.Companion.toUiMove
import io.github.sophon.fightingnerd.feat.moveList.usecase.LoadMoveListDataUseCase
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


    private fun loadData() {
        viewModelScope.launch {
            loadMoveListDataUseCase.invoke(gameId = gameId, characterId = characterId)
                .onSuccess { (character, moveList) ->
                    _state.update { state ->
                        val fullMoveList = moveList.associateBy { it.id }
                        val uiMoveList = moveList.map { it.toUiMove() }
                        state.copy(
                            character = character,
                            fullMoveList = fullMoveList,
                            uiMoveList = uiMoveList,
                        )
                    }
                }
                .onError { Napier.e(tag = TAG) { "loadData: $it" } }
        }
    }


    private companion object {
        const val TAG = "MoveListVM"
    }
}