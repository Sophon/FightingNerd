package io.github.sophon.fightingnerd.feat.more.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsState
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ConfirmFeatureChangeDialog(
    gameList: ImmutableList<FeatureSettingsState.UiFeatureSetting.UiGame>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
                .background(nerdColorPalette.surfaceHigh)
                .padding(nerdDimensions.dialogPadding)
        ) {
            Text(
                text = "Are you sure?",
                style = nerdTypography.headlineMedium,
                color = nerdColorPalette.textPrimary,
            )

            Text(
                text = "Disabling a game wipes its offline data.",
                style = nerdTypography.bodyLarge,
                color = nerdColorPalette.textPrimary,
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = nerdColorPalette.surfaceHigh,
                        contentColor = nerdColorPalette.textPrimary,
                    ),
                ) {
                    Text(
                        text = "Confirm",
                        style = nerdTypography.labelLarge,
                    )
                }

                OutlinedButton(
                    onClick = onDismiss,
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
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun ConfirmFeatureChangePreview() {
    FightingNerdTheme {
        ConfirmFeatureChangeDialog(
            gameList = persistentListOf(
                FeatureSettingsState.UiFeatureSetting.UiGame(displayName = "Tekken 8", id = "T8", isEnabled = false),
                FeatureSettingsState.UiFeatureSetting.UiGame(displayName = "Street Fighter 6", id = "SF6", isEnabled = false),
                FeatureSettingsState.UiFeatureSetting.UiGame(displayName = "Guilty Gear Strive", id = "GGST", isEnabled = false),
            ),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
//endregion
