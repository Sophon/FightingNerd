package io.github.sophon.fightingnerd.feat.quiz.ui.quiz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.quiz_frame_dat_label_counter
import fightingnerd.composeapp.generated.resources.quiz_frame_dat_label_on_block
import fightingnerd.composeapp.generated.resources.quiz_frame_dat_label_on_hit
import fightingnerd.composeapp.generated.resources.quiz_frame_dat_label_startup
import fightingnerd.composeapp.generated.resources.quiz_frame_data_label_no_media
import io.github.sophon.fightingnerd.core.ui.components.CircularLoader
import io.github.sophon.fightingnerd.feat.quiz.model.Question
import io.github.sophon.fightingnerd.feat.quiz.ui.quiz.QuizState
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.stringResource
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
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "${question.characterName}: ${move.input}",
            color = nerdColorPalette.textPrimary,
            style = nerdTypography.headlineSmall,
        )
        Spacer(Modifier.height(nerdDimensions.componentPadding))

        when {
            (move.urls?.videoUrl != null) -> {
                VideoPlayer(videoUrl = move.urls.videoUrl)
            }
            (move.urls?.hitboxImageList.orEmpty().isNotEmpty() || move.urls?.moveImageList.orEmpty().isNotEmpty()) -> {
                Image(url = move.urls)
            }
            else -> {
                NoMedia()
            }
        }

        Options(
            question = question,
            onAnswer = onAnswer,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Image(
    url: Question.MoveOption.Urls?,
    modifier: Modifier = Modifier
) {
    val image = url?.hitboxImageList?.firstOrNull()
        ?: url?.moveImageList?.firstOrNull()
        ?: return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .background(nerdColorPalette.surface),
    ) {
        SubcomposeAsyncImage(
            model = image,
            contentDescription = null,
            loading = { CircularLoader() },
            modifier = Modifier.align(Alignment.Center),
        )
    }
    Spacer(Modifier.height(nerdDimensions.componentPadding))
}

@Composable
private fun NoMedia(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.quiz_frame_data_label_no_media),
        color = nerdColorPalette.textSecondary,
        style = nerdTypography.titleLarge,
        modifier = modifier,
    )
}

@Composable
private fun Options(
    question: Question,
    onAnswer: (answerIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAnswered = question.answeredIndex != null

    Column(
        verticalArrangement = Arrangement.spacedBy(nerdDimensions.inlineGap),
        modifier = modifier,
    ) {
        question.options.chunked(2).forEachIndexed { rowIndex, rowOptions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(nerdDimensions.inlineGap),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
            ) {
                rowOptions.forEach { move ->
                    val isCorrect = isAnswered && (move == question.correct)
                    val isWrong = isAnswered && (move == question.answered) && move != question.correct

                    Option(
                        move = move,
                        isCorrect = isCorrect,
                        isWrong = isWrong,
                        isEnabled = isAnswered.not(),
                        onClick = { onAnswer(question.options.indexOf(move)) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
            }
        }
    }
}

@Composable
private fun Option(
    move: Question.MoveOption,
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
            .height(170.dp)
            .padding(nerdDimensions.componentGap),
    ) {
        FrameData(
            label = stringResource(Res.string.quiz_frame_dat_label_startup),
            body = move.startup,
            textColor = textColor,
        )
        FrameData(
            label = stringResource(Res.string.quiz_frame_dat_label_on_block),
            body = move.onBlock,
            textColor = textColor,
        )
        FrameData(
            label = stringResource(Res.string.quiz_frame_dat_label_on_hit),
            body = move.onHit,
            textColor = textColor,
        )
        FrameData(
            label = stringResource(Res.string.quiz_frame_dat_label_counter),
            body = move.onCH,
            textColor = textColor,
        )
    }
}

@Composable
private fun FrameData(
    label: String,
    body: String?,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val style = nerdTypography.titleMedium
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            color = textColor,
            style = style,
            textAlign = TextAlign.Start,
        )

        Text(
            text = body ?: "-",
            color = textColor,
            style = style,
            textAlign = TextAlign.End,
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