package io.github.sophon.fightingnerd.feat.moveList.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.moveList.model.Property
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