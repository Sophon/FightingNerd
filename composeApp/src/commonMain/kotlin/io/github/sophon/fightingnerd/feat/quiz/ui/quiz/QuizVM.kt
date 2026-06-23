package io.github.sophon.fightingnerd.feat.quiz.ui.quiz

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class QuizVM(): ViewModel() {
    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    fun nextQuestion() {}

    fun answer() {}


    private fun loadMoveList() {}
}