package io.github.sophon.fightingnerd.feat.quiz.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.core.ui.Dialog
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.feat.quiz.ui.quiz.components.FinishDialog
import io.github.sophon.fightingnerd.feat.quiz.usecase.GenerateQuestionsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class QuizVM(
    private val gameId: String,
    private val characterId: String?,
    private val onExit: () -> Unit,

    private val overlayService: OverlayService,
    private val generateQuestionsUseCase: GenerateQuestionsUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()


    init {
        loadMoveList()
    }


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

        _state.update { state ->
            val updatedQuestionList = state.questionList
                .toMutableList()
                .apply {
                    this[state.currentQuestionIndex] = currentQuestion.copy(answeredIndex = answerIndex)
                }
                .toImmutableList()
            val isCorrect = (answerIndex == currentQuestion.correctIndex)
            val correct = state.correct + if (isCorrect) 1 else 0
            val incorrect = state.incorrect + if (isCorrect.not()) 1 else 0

            state.copy(
                questionList = updatedQuestionList,
                correct = correct,
                incorrect = incorrect,
                displayFinishDialog = state.isLastQuestion,
            )
        }

        if (state.isLastQuestion) {
            finishQuiz()
        }
    }


    private fun loadMoveList() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            generateQuestionsUseCase(gameId = gameId, characterId = characterId)
                .onSuccess { questionList ->
                    _state.update { it.copy(questionList = questionList.toImmutableList()) }
                }
                .onError { error ->
                    Napier.e(tag = TAG) { "loadMoveList: $error" }
                    overlayService.show(error)
                }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun finishQuiz() {
        overlayService.show(
            Dialog { onDismiss ->
                FinishDialog(
                    correctCount = state.value.correct,
                    incorrectCount = state.value.incorrect,
                    onExit = {
                        _state.update { it.copy(displayFinishDialog = false) }
                        onDismiss()
                        onExit()
                    }
                )
            }
        )
    }


    private companion object {
        const val TAG = "QuizVM"
    }
}
