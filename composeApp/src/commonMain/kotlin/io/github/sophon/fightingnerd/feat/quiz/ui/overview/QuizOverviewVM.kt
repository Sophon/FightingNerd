package io.github.sophon.fightingnerd.feat.quiz.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.fightingnerd.feat.quiz.usecase.SubscribeToGamesWithDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class QuizOverviewVM(
    private val subscribeToGamesWithDataUseCase: SubscribeToGamesWithDataUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(QuizOverviewState())
    val state = _state.asStateFlow()


    init {
        loadWidgets()
    }


    private fun loadWidgets() {
        viewModelScope.launch {
            subscribeToGamesWithDataUseCase.invoke().collect { gameWidgetList ->
                val currentWidgetList = _state.value.quizGameWidgetList
                val currentIds = currentWidgetList.map { it.game.id }.toSet()
                val newIds = gameWidgetList.map { (game, _) -> game.id }.toSet()

                val kept = currentWidgetList.filter { it.game.id in newIds }
                val added = gameWidgetList.filterNot { (game, _) -> game.id in currentIds }
                val merged = kept + added

                _state.update { it.copy(quizGameWidgetList = merged) }
            }
        }
    }


    private companion object {
        const val TAG = "QuizOverviewVM"
    }
}