package io.github.sophon.fightingnerd.adapter.inbound.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.adapter.inbound.quiz.model.QuizGameWidget
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.components.CharacterCard
import io.github.sophon.fightingnerd.inPort.CheckCharacterHasMovesUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToCharactersUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToGamesUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

internal class QuizOverviewVM(
    private val overlayService: OverlayService,
    private val subscribeToGamesUseCase: SubscribeToGamesUseCase,
    private val subscribeToCharactersUseCase: SubscribeToCharactersUseCase,
    private val checkCharacterHasMovesUseCase: CheckCharacterHasMovesUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(QuizOverviewState())
    val state = flow {
        coroutineScope {
            launch { subscribeToEnabledGames() }
            emitAll(_state)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QuizOverviewState(),
    )


    fun onExpandWidget(game: Game) {
        _state.update { state ->
            val updatedList = state.quizGameWidgetList.map { widget ->
                when {
                    (widget.game == game) -> {
                        widget.copy(isExpanded = widget.isExpanded.not())
                    }
                    widget.isExpanded -> {
                        widget.copy(isExpanded = false)
                    } else -> {
                        widget
                    }
                }
            }
            val updatedState = state.copy(quizGameWidgetList = updatedList.toImmutableList())
            updatedState
        }
    }


    private suspend fun subscribeToEnabledGames() {
        subscribeToGamesUseCase().collectLatest { result ->
            result
                .onSuccess { gameList ->
                    val existingByGame = _state.value.quizGameWidgetList.associateBy { it.game }
                    val widgetList = gameList.map { game ->
                        val existing = existingByGame[game]
                        val widget = QuizGameWidget(
                            game = game,
                            characterList = existing?.characterList ?: persistentListOf(),
                            isExpanded = existing?.isExpanded ?: false,
                        )
                        widget
                    }.toImmutableList()
                    _state.update { it.copy(quizGameWidgetList = widgetList) }
                    subscribeToCharacters(widgets = widgetList)
                }
                .onError { error ->
                    overlayService.show(error)
                }
        }
    }

    private suspend fun subscribeToCharacters(widgets: List<QuizGameWidget>) {
        coroutineScope {
            widgets.forEach { gameWidget ->
                launch {
                    subscribeToCharactersUseCase(gameWidget.game).collectLatest { characterList ->
                        val newState = _state.updateAndGet { state ->
                            val updatedList = state.quizGameWidgetList.map { widget ->
                                if (widget.game == gameWidget.game) {
                                    val existingCardsById = widget.characterList.associateBy { it.id }
                                    val newCardList = characterList.map { domainCharacter ->
                                        val existing = existingCardsById[domainCharacter.id]
                                        val card = CharacterCard(
                                            id = domainCharacter.id,
                                            displayName = domainCharacter.displayName,
                                            iconUrl = domainCharacter.iconUrl,
                                            isLoading = existing?.isLoading ?: true,
                                        )
                                        card
                                    }.toImmutableList()
                                    val merged = widget.copy(characterList = newCardList)
                                    merged
                                } else {
                                    widget
                                }
                            }
                            val updatedState = state.copy(quizGameWidgetList = updatedList.toImmutableList())
                            updatedState
                        }

                        val updatedWidget = newState.quizGameWidgetList.first { it.game == gameWidget.game }
                        checkForMoveList(gameWidget = updatedWidget)
                    }
                }
            }
        }
    }

    private suspend fun checkForMoveList(gameWidget: QuizGameWidget) {
        coroutineScope {
            gameWidget.characterList.forEach { character ->
                launch {
                    checkCharacterHasMovesUseCase.invoke(
                        game = gameWidget.game,
                        characterId = character.id,
                    ).collect { hasMoves ->
                        if (hasMoves.not()) return@collect
                        _state.update { state ->
                            val updatedList = state.quizGameWidgetList.map { widget ->
                                if (widget.game == gameWidget.game) {
                                    widget.withPlayableCharacter(characterId = character.id)
                                } else {
                                    widget
                                }
                            }
                            val updatedState = state.copy(quizGameWidgetList = updatedList.toImmutableList())
                            updatedState
                        }
                    }
                }
            }
        }
    }
}
