package io.github.sophon.fightingnerd.feat.moveList.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.MoveRepository
import io.github.sophon.fightingnerd.feat.home.usecase.LoadMoveListUseCase
import io.github.sophon.fightingnerd.feat.moveList.model.Property
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

internal class MoveListVM(
    gameId: String,
    characterId: String,
    private val loadMoveListUseCase: LoadMoveListUseCase,
    private val moveRepository: MoveRepository,
): ViewModel() {
    private val _state = MutableStateFlow(MoveListState(null, null))
    val state = _state
        .onStart {
            loadGameFromId(gameId)
            loadMoveList()
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

    private fun loadMoveList() {
        val moveList = moveRepository.moveList
        Napier.d(tag = TAG) { "Moves loaded: ${moveList.size}" }
        val uiMoveList = moveList.map { it.toUiMove() }
        _state.update { state ->
            state.copy(
                fullMoveList = moveList.associateBy { it.id },
                uiMoveList = uiMoveList,
            )
        }
    }

    private fun Move.toUiMove(): MoveListState.UiMove {
        return MoveListState.UiMove(
            id = id,
            input = input,
            startup = startup,
            level = guard,
            onHit = onHit,
            onBlock = onBlock,
            onCounter = onCH,
            propertySet = buildSet {
                invulnerability?.let { add(Property.Invincible) }
                t8Properties?.let { props ->
                    if (props.isHeat) add(Property.Heat)
                    if (props.isHoming) add(Property.Homing)
                    if (props.isPowerCrush) add(Property.PowerCrush)
                    if (props.isHighCrush) add(Property.HighCrush)
                    if (props.isLowCrush) add(Property.LowCrush)
                }
            },
        )
    }


    private companion object {
        const val TAG = "MoveListVM"
    }
}