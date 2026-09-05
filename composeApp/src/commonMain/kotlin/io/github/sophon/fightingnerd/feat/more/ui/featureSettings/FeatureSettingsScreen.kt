package io.github.sophon.fightingnerd.feat.more.ui.featureSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.more_feature_settings_btn_save
import io.github.sophon.fightingnerd.core.ui.components.TopBarButton
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun FeatureSettingsScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<FeatureSettingsVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onExit = onExit,
        onFeatureToggle = vm::toggleFeature,
        onGameToggle = vm::toggleGame,
        onSaveConfig = vm::displayConfirmationDialog,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: FeatureSettingsState,
    onExit: () -> Unit,
    onFeatureToggle: (featureIndex: Int, isEnabled: Boolean) -> Unit,
    onGameToggle: (featureIndex: Int, gameIndex: Int, isEnabled: Boolean) -> Unit,
    onSaveConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = nerdDimensions.screenPaddingHorizontal,
                vertical = nerdDimensions.screenPaddingVertical,
            )
    ) {
        Header(
            isChanged = state.isChanged,
            onExit = onExit,
            onSaveConfig = onSaveConfig
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(
                space = nerdDimensions.inlineGap,
                alignment = Alignment.Top,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(state.updatedFeatureList) { featureIndex, feature ->
                val shape = RoundedCornerShape(nerdDimensions.cornerDefault)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(nerdColorPalette.surface)
                ) {
                    Toggle(
                        title = feature.featureName,
                        subtitle = feature.version,
                        isEnabled = feature.isEnabled,
                        isCategory = true,
                        onToggle = { onFeatureToggle(featureIndex, it) },
                    )

                    feature.gameList.forEachIndexed { gameIndex, game ->
                        Toggle(
                            title = game.displayName,
                            isEnabled = game.isEnabled,
                            onToggle = {
                                onGameToggle(featureIndex, gameIndex, it)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    isChanged: Boolean,
    onExit: () -> Unit,
    onSaveConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        TopBarButton(onClick = onExit)

        TextButton(
            onClick = onSaveConfig,
            colors = ButtonDefaults.textButtonColors(
                contentColor = nerdColorPalette.textPrimary,
                disabledContentColor = nerdColorPalette.textDisabled,
            ),
            enabled = isChanged,
        ) {
            Text(
                text = stringResource(Res.string.more_feature_settings_btn_save).uppercase(),
                style = nerdTypography.labelLarge,
            )
        }
    }
}

@Composable
private fun Toggle(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    subtitle: String? = null,
    isCategory: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(nerdDimensions.componentPadding),
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = if (isCategory) nerdTypography.titleLarge else nerdTypography.titleSmall,
                color = nerdColorPalette.textPrimary,
            )
            Spacer(Modifier.height(nerdDimensions.inlineGap))

            subtitle?.let {
                Text(
                    text = it,
                    style = nerdTypography.bodySmall,
                    color = nerdColorPalette.textTertiary,
                )
            }
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
        )
    }
}


//region PREVIEW
@Composable
@Preview
private fun FeatureSettingsPreview() {
    FightingNerdTheme {
        Content(
            state = FeatureSettingsState.PREVIEW,
            onExit = {},
            onFeatureToggle = { _, _ -> },
            onGameToggle = { _, _, _, -> },
            onSaveConfig = {},
        )
    }
}
//endregion