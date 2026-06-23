package io.github.sophon.fightingnerd.feat.quiz.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.fightingnerd.LocalBottomBarPadding
import io.github.sophon.fightingnerd.core.ui.components.LoadingContent
import io.github.sophon.fightingnerd.core.ui.components.TopBarButton
import io.github.sophon.fightingnerd.feat.quiz.COUNT_QUESTIONS
import io.github.sophon.fightingnerd.feat.quiz.ui.quiz.components.QuestionSection
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun QuizScreen(
    gameId: String,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<QuizVM>(
        parameters = { parametersOf(gameId) }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onExit = onExit,
        onAnswer = vm::answer,
        onClickNext = vm::nextQuestion,
        onClickBack = vm::previousQuestion,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: QuizState,
    onExit: () -> Unit,
    onAnswer: (answerIndex: Int) -> Unit,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            QuizTopBar(
                currentQuestionIndex = state.currentQuestionIndex,
                onQuit = onExit,
            )
        },
        modifier = modifier
            .fillMaxSize()
            .background(nerdColorPalette.background)
            .padding(
                horizontal = nerdDimensions.screenPaddingHorizontal,
                vertical = nerdDimensions.screenPaddingVertical,
            )
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingContent()
        } else {
            Column (
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(LocalBottomBarPadding.current),
            ) {
                state.currentQuestion?.let { question ->
                    QuestionSection(
                        question = question,
                        onAnswer = onAnswer,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(nerdDimensions.componentPadding))

                NavigationSection(
                    onClickNext = onClickNext,
                    onClickBack = onClickBack,
                )
            }
        }
    }
}

@Composable
private fun QuizTopBar(
    currentQuestionIndex: Int,
    onQuit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
    ) {
        TopBarButton(onClick = onQuit)
        Spacer(Modifier.width(nerdDimensions.inlineGap))

        Text(
            text = "${currentQuestionIndex + 1}/$COUNT_QUESTIONS",
            style = nerdTypography.displaySmall,
            color = nerdColorPalette.textPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun NavigationSection(
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
    ) {
        NavigationButton(
            onClick = onClickBack,
            icon = Icons.Outlined.ArrowCircleLeft,
        )

        NavigationButton(
            onClick = onClickNext,
            icon = Icons.Outlined.ArrowCircleRight,
        )
    }
}

@Composable
private fun NavigationButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = nerdDimensions.screenPaddingHorizontal)
            .clip(CircleShape)
            .size(nerdDimensions.iconLarge)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = nerdColorPalette.textPrimary,
            modifier = Modifier
                .size(nerdDimensions.iconLarge)
        )
    }
}


//region PREVIEW
@Composable
@Preview()
private fun QuizPreview() {
    FightingNerdTheme {
        Content(
            state = QuizState.PREVIEW,
            onAnswer = {},
            onClickNext = {},
            onClickBack = {},
            onExit = {},
        )
    }
}
//endregion