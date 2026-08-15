package io.github.sophon.fightingnerd.feat.quiz.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.fightingnerd.feat.quiz.usecase.SubscribeGameWidgetsUseCase
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


    private fun loadWidgets() {
        viewModelScope.launch {
            subscribeGameWidgetsUseCase.invoke().collect { gameWidgetList ->
                _state.update { it.copy(quizGameWidgetList = gameWidgetList) }
            }
        }
    }


    private companion object {
        const val TAG = "QuizOverviewVM"
    }
}