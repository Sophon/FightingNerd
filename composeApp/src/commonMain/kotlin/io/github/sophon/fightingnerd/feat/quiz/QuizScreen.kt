package io.github.sophon.fightingnerd.feat.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun QuizScreen(
    //TODO: navigate to quiz
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<QuizVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    //content
}