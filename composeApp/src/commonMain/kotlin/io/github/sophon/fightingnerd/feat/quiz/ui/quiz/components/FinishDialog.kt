package io.github.sophon.fightingnerd.feat.quiz.ui.quiz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography

@Composable
internal fun FinishDialog(
    correctCount: Int,
    incorrectCount: Int,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onExit,
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
                .background(nerdColorPalette.surfaceHigh)
                .padding(nerdDimensions.dialogPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Correct: $correctCount",
                style = nerdTypography.headlineMedium,
                color = Color.Green,
            )

            Text(
                text = "Incorrect: $incorrectCount",
                style = nerdTypography.headlineMedium,
                color = nerdColorPalette.error,
            )

            Spacer(
                modifier = Modifier.height(nerdDimensions.sectionGap),
            )

            OutlinedButton(
                onClick = onExit,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = nerdColorPalette.surfaceHigh,
                    contentColor = nerdColorPalette.textPrimary,
                ),
            ) {
                Text(
                    text = "Exit",
                    style = nerdTypography.labelLarge,
                )
            }
        }
    }
}
