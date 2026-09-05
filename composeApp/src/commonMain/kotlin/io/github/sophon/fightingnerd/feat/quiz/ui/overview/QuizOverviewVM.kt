package io.github.sophon.fightingnerd.feat.quiz.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.feat.quiz.usecase.SubscribeGameWidgetsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class QuizOverviewVM(
    private val subscribeGameWidgetsUseCase: SubscribeGameWidgetsUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(QuizOverviewState())
    val state = _state.asStateFlow()


    init {
        loadWidgets()
    }


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


    private fun loadWidgets() {
        viewModelScope.launch {
            subscribeGameWidgetsUseCase.invoke().collect { gameWidgetList ->
                _state.update { previous ->
                    val expandedByGame = previous.quizGameWidgetList.associate { it.game to it.isExpanded }
                    val merged = gameWidgetList.map { widget ->
                        widget.copy(isExpanded = expandedByGame[widget.game] ?: false)
                    }.toImmutableList()
                    previous.copy(quizGameWidgetList = merged)
                }
            }
        }
    }


    private companion object {
        const val TAG = "QuizOverviewVM"
    }
}
