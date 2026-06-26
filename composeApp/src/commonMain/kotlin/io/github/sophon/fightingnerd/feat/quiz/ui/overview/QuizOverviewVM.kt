package io.github.sophon.fightingnerd.feat.quiz.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.feat.home.usecase.LoadEmptyWidgetsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class QuizOverviewVM(
    private val overlayService: OverlayService,
    private val loadEmptyWidgetsUseCase: LoadEmptyWidgetsUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(QuizOverviewState())
    val state = _state.asStateFlow()


    init {
        loadWidgets()
    }


    private fun loadWidgets() {
        viewModelScope.launch {
            loadEmptyWidgetsUseCase.invoke().collect { result ->
                result
                    .onSuccess { loadedWidgetList ->
                        val currentWidgetList = _state.value.gameWidgetList
                        val currentIds = currentWidgetList.map { it.game.id }.toSet()
                        val newIds = loadedWidgetList.map { it.game.id }.toSet()

                        val kept = currentWidgetList.filter { it.game.id in newIds }
                        val added = loadedWidgetList.filterNot { it.game.id in currentIds }
                        val merged = kept + added

                        _state.update { it.copy(gameWidgetList = merged) }
                    }
                    .onError { error ->
                        Napier.e(tag = TAG) { "loadWidgets(): $error" }
                        overlayService.show(error)
                    }
            }
        }
    }


    private companion object {
        const val TAG = "QuizOverviewVM"
    }
}