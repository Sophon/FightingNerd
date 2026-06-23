package io.github.sophon.fightingnerd.feat.quiz.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.feat.quiz.usecase.GenerateQuestionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class QuizVM(
    private val gameId: String,

    private val overlayService: OverlayService,
    private val generateQuestionsUseCase: GenerateQuestionsUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()


    init {
        loadMoveList()
    }


    fun onQuit() {}

    fun nextQuestion() {
        if (state.value.isLastQuestion) {
            finishQuiz()
            return
        }

        _state.update { it.copy(currentQuestionIndex = (it.currentQuestionIndex + 1)) }
    }

    fun previousQuestion() {
        if (state.value.currentQuestionIndex == 0) return

        _state.update { it.copy(currentQuestionIndex = (it.currentQuestionIndex - 1)) }
    }

    fun answer(answerIndex: Int) {
        val state = state.value
        val currentQuestion = state.questionList.getOrNull(state.currentQuestionIndex)
        if (currentQuestion == null) {
            overlayService.show(Toast(message = "No current question", type = Toast.Type.ERROR))
            return
        }
        if (currentQuestion.answeredIndex != null) {
            overlayService.show(Toast(message = "Question already answered", type = Toast.Type.INFO))
            return
        }

        _state.update {
            val updatedQuestionList = it.questionList.toMutableList().apply {
                this[it.currentQuestionIndex] = currentQuestion.copy(answeredIndex = answerIndex)
            }
            val isCorrect = answerIndex == currentQuestion.correctIndex
            val correct = it.correct + if (isCorrect) 1 else 0
            val incorrect = it.incorrect + if (isCorrect) 1 else 0

            it.copy(
                questionList = updatedQuestionList,
                correct = correct,
                incorrect = incorrect,
            )
        }
    }


    private fun loadMoveList() {
        viewModelScope.launch {
            generateQuestionsUseCase.invoke(gameId = gameId)
                .onSuccess { questionList ->
                    _state.update { it.copy(questionList = questionList) }
                }
                .onError { error ->
                    Napier.e(tag = TAG) { "loadMoveList: $error" }
                    overlayService.show(error)
                }
        }
    }

    private fun finishQuiz() {
        //display dialog
        //navigate back to Overview
    }


    private companion object {
        const val TAG = "QuizVM"
    }
}
