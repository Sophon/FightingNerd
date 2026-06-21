package io.github.sophon.fightingnerd.feat.quiz

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class QuizVM(): ViewModel() {
    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    //
}