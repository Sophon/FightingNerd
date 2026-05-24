package io.github.sophon.fightingnerd.feat.moveList

import androidx.lifecycle.ViewModel
import io.github.aakira.napier.Napier
import io.github.sophon.core.feature.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class MoveListVM(
    gameId: String,
    characterId: String,
): ViewModel() {
    private val _state = MutableStateFlow(MoveListState(null, null))
    val state = _state.asStateFlow()

    init {
        loadGameFromId(gameId)
        loadCharacterFromId(characterId)
    }


    private fun loadGameFromId(gameId: String) {
        val game = Game.fromId(gameId)
        if (game == null) {
            Napier.e(tag = TAG) { "loadGameFromId: $gameId -> $game" }
        } else {
            _state.update { it.copy(game = game) }
        }
    }

    private fun loadCharacterFromId(characterId: String) {
        //TODO:
    }


    private companion object {
        const val TAG = "MoveListVM"
    }
}