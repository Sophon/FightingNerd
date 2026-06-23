package io.github.sophon.fightingnerd.feat.quiz.ui.quiz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.quiz.model.Question
import io.github.sophon.fightingnerd.feat.quiz.ui.quiz.QuizState
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun QuestionSection(
    question: Question,
    onAnswer: (answerIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val move = question.correct

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier,
    ) {
        Text(
            text = "${move.characterId}: ${move.input}",
            color = nerdColorPalette.textPrimary,
            style = nerdTypography.headlineSmall,
        )

        move.urls.videoId?.let { videoId ->
            //TODO: replace with video down the line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(nerdColorPalette.surface),
            )
        }

        Options(
            question = question,
            onAnswer = onAnswer,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Options(
    question: Question,
    onAnswer: (answerIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelList = listOf("A", "B", "C", "D")
    val isAnswered = question.answeredIndex != null

    Column(
        verticalArrangement = Arrangement.spacedBy(nerdDimensions.inlineGap),
        modifier = modifier,
    ) {
        question.options.chunked(2).forEachIndexed { rowIndex, rowOptions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(nerdDimensions.inlineGap),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowOptions.forEachIndexed { colIndex, move ->
                    val index = (rowIndex * 2 + colIndex)
                    val isCorrect = isAnswered && (index == question.correctIndex)
                    val isWrong = (isAnswered && index == question.answeredIndex)
                            && (index != question.correctIndex)
                    Option(
                        label = labelList[index],
                        move = move,
                        isCorrect = isCorrect,
                        isWrong = isWrong,
                        isEnabled = isAnswered.not(),
                        onClick = { onAnswer(index) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun Option(
    label: String,
    move: Move,
    isCorrect: Boolean,
    isWrong: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background: Color
    val textColor: Color
    when {
        isCorrect -> {
            background = Color.Green
            textColor = Color.Black
        }
        isWrong -> {
            background = nerdColorPalette.error
            textColor = nerdColorPalette.textPrimary
        }
        else -> {
            background = nerdColorPalette.surface
            textColor = nerdColorPalette.textPrimary
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .background(background)
            .clickable(enabled = isEnabled, onClick = onClick)
            .heightIn(min = 120.dp)
            .padding(16.dp),
    ) {
        Text(
            text = label,
            color = textColor,
            style = nerdTypography.displaySmall,
        )
        Spacer(Modifier.height(nerdDimensions.inlineGap))

        Text(
            text = "${move.startup} / ${move.onBlock} / ${move.onHit} / ${move.onCH}",
            color = textColor,
            style = nerdTypography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}


//region PREVIEW
@Preview
@Composable
private fun PreviewQuestionSectionWithVideo() {
    FightingNerdTheme {
        Surface {
            QuestionSection(
                question = QuizState.PREVIEW.questionList[0],
                onAnswer = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewQuestionSectionNoVideo() {
    FightingNerdTheme {
        Surface {
            QuestionSection(
                question = QuizState.PREVIEW.questionList[1],
                onAnswer = {},
            )
        }
    }
}
//endregion