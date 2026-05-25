package io.github.sophon.fightingnerd.feat.moveList.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.Game
import io.github.sophon.fightingnerd.feat.moveList.usecase.LoadMoveListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MoveListVM(
    gameId: String,
    characterId: String,
    private val loadMoveListUseCase: LoadMoveListUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(MoveListState(null, null))
    val state = _state
        .onStart {
            loadGameFromId(gameId)
            loadCharacterFromId(characterId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MoveListState(null, null),
        )


    private fun loadGameFromId(gameId: String) {
        val game = Game.fromId(gameId)
        if (game == null) {
            Napier.e(tag = TAG) { "loadGameFromId: $gameId -> $game" }
        } else {
            _state.update { it.copy(game = game) }
        }
    }

    private fun loadCharacterFromId(characterId: String) {
        val game = _state.value.game ?: return
        viewModelScope.launch {
            loadMoveListUseCase.invoke(
                game = game,
                characterId = characterId,
            )
                .onSuccess {
                    Napier.d(tag = TAG) { "Moves loaded: ${it.size}" }
                }
                .onError {
                    //TODO: error event
                }
        }
    }


    private companion object {
        const val TAG = "MoveListVM"
    }
}