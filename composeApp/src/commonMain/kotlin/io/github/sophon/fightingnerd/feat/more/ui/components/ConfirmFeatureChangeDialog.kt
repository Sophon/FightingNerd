package io.github.sophon.fightingnerd.feat.more.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.general_confirm
import fightingnerd.composeapp.generated.resources.general_dismiss
import fightingnerd.composeapp.generated.resources.more_feature_settings_dialog_btn_wipe
import fightingnerd.composeapp.generated.resources.more_feature_settings_dialog_desc
import fightingnerd.composeapp.generated.resources.more_feature_settings_dialog_title
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsState
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
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
            verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentGap),
            modifier = modifier
                .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
                .background(nerdColorPalette.surfaceHigh)
                .padding(nerdDimensions.dialogPadding)
        ) {
            Text(
                text = stringResource(Res.string.more_feature_settings_dialog_title),
                style = nerdTypography.headlineSmall,
                color = nerdColorPalette.textPrimary,
            )

            Text(
                text = stringResource(Res.string.more_feature_settings_dialog_desc),
                style = nerdTypography.bodyLarge,
                color = nerdColorPalette.textPrimary,
            )

            GameList(
                gameList = gameList,
                modifier = Modifier.fillMaxWidth(),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentGapTight),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = nerdColorPalette.surfaceHigh,
                        contentColor = nerdColorPalette.textPrimary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(Res.string.general_dismiss),
                        style = nerdTypography.labelLarge,
                    )
                }

                OutlinedButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = nerdColorPalette.surfaceHigh,
                        contentColor = nerdColorPalette.error,
                    ),
                    border = BorderStroke(nerdDimensions.strokeThin, nerdColorPalette.error),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(Res.string.more_feature_settings_dialog_btn_wipe),
                        style = nerdTypography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun GameList(
    gameList: ImmutableList<FeatureSettingsState.UiFeatureSetting.UiGame>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentGapTight),
        contentPadding = PaddingValues(nerdDimensions.componentPadding),
        modifier = modifier
            .heightIn(max = 200.dp)
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .border(
                width = nerdDimensions.strokeThin,
                color = nerdColorPalette.divider,
                shape = RoundedCornerShape(nerdDimensions.cornerDefault),
            ),
    ) {
        items(items = gameList, key = { it.id }) { game ->
            Text(
                text = "- ${game.displayName}",
                style = nerdTypography.bodyMedium,
                color = nerdColorPalette.textPrimary,
            )
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
