package io.github.sophon.fightingnerd.feat.more.ui.updates

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.general_save
import fightingnerd.composeapp.generated.resources.more_updates_settings_auto_update_desc
import fightingnerd.composeapp.generated.resources.more_updates_settings_auto_update_title
import fightingnerd.composeapp.generated.resources.more_updates_settings_last_updated
import fightingnerd.composeapp.generated.resources.more_updates_settings_period_label
import io.github.sophon.fightingnerd.core.ui.components.CircularLoader
import io.github.sophon.fightingnerd.core.ui.components.TopBarButton
import io.github.sophon.fightingnerd.feat.more.ui.updates.UpdatesState.UiFeatureSetting
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UpdatesScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<UpdatesVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onExit = onExit,
        onToggleAutoUpdates = vm::toggleEnableAutoUpdate,
        onSetPeriod = vm::setPeriod,
        onSetUnit = vm::setUnit,
        onSave = vm::save,
        onRefreshFeature = vm::refreshWiki,
        onRefreshGame = vm::refreshGame,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: UpdatesState,
    onExit: () -> Unit,
    onToggleAutoUpdates: (Boolean) -> Unit,
    onSetPeriod: (String) -> Unit,
    onSetUnit: (Int) -> Unit,
    onSave: () -> Unit,
    onRefreshFeature: (String) -> Unit,
    onRefreshGame: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .padding(
                horizontal = nerdDimensions.screenPaddingHorizontal,
                vertical = nerdDimensions.screenPaddingVertical,
            )
    ) {
        Header(
            isChanged = state.isChanged,
            onExit = onExit,
            onSave = onSave,
        )

        AutoUpdateToggle(
            isEnabled = state.updatedAutoUpdateSettings.isEnabled,
            period = state.updatedAutoUpdateSettings.period,
            unit = state.updatedAutoUpdateSettings.unit,
            onToggle = onToggleAutoUpdates,
            onSetPeriod = onSetPeriod,
            onSetUnit = onSetUnit,
        )
        Spacer(Modifier.height(nerdDimensions.sectionGap))

        GameList(
            featureList = state.featureList,
            onRefreshFeature = onRefreshFeature,
            onRefreshGame = onRefreshGame,
        )
    }
}

@Composable
private fun Header(
    isChanged: Boolean,
    onExit: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        TopBarButton(onClick = onExit)

        TextButton(
            onClick = onSave,
            colors = ButtonDefaults.textButtonColors(
                contentColor = nerdColorPalette.textPrimary,
                disabledContentColor = nerdColorPalette.textDisabled,
            ),
            enabled = isChanged,
        ) {
            Text(
                text = stringResource(Res.string.general_save).uppercase(),
                style = nerdTypography.labelLarge,
            )
        }
    }
}

@Composable
private fun AutoUpdateToggle(
    isEnabled: Boolean,
    period: Int?,
    unit: UpdatesState.AutoUpdateSettings.TimeUnit,
    onToggle: (Boolean) -> Unit,
    onSetPeriod: (String) -> Unit,
    onSetUnit: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(nerdDimensions.componentPadding),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.more_updates_settings_auto_update_title),
                    style = nerdTypography.titleLarge,
                    color = nerdColorPalette.textPrimary,
                )
                Spacer(Modifier.height(nerdDimensions.inlineGap))

                Text(
                    text = stringResource(Res.string.more_updates_settings_auto_update_desc),
                    style = nerdTypography.bodyMedium,
                    color = nerdColorPalette.textSecondary,
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
            )
        }

        if (isEnabled) {
            Spacer(Modifier.height(nerdDimensions.inlineGap))
            PeriodRow(
                period = period,
                unit = unit,
                onSetPeriod = onSetPeriod,
                onSetUnit = onSetUnit,
            )
        }
    }
}

@Composable
private fun PeriodRow(
    period: Int?,
    unit: UpdatesState.AutoUpdateSettings.TimeUnit,
    onSetPeriod: (String) -> Unit,
    onSetUnit: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(Res.string.more_updates_settings_period_label),
            style = nerdTypography.bodyLarge,
            color = nerdColorPalette.textPrimary,
            modifier = Modifier.weight(1f),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(nerdDimensions.inlineGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var textFieldValue by remember(period) {
                val text = period?.toString().orEmpty()
                mutableStateOf(TextFieldValue(text, TextRange(text.length)))
            }
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onSetPeriod(newValue.text)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .size(width = 60.dp, height = 50.dp)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            textFieldValue = textFieldValue.copy(
                                selection = TextRange(0, textFieldValue.text.length),
                            )
                        }
                    },
            )

            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.width(110.dp)) {
                TextButton(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = nerdColorPalette.textPrimary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(unit.stringResource).uppercase(),
                        style = nerdTypography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    UpdatesState.AutoUpdateSettings.TimeUnit.entries.forEachIndexed { index, timeUnit ->
                        DropdownMenuItem(
                            text = { Text(stringResource(timeUnit.stringResource).uppercase()) },
                            onClick = {
                                onSetUnit(index)
                                expanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameList(
    featureList: ImmutableList<UiFeatureSetting>,
    onRefreshFeature: (String) -> Unit,
    onRefreshGame: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(
            space = nerdDimensions.inlineGap,
            alignment = Alignment.Top,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(featureList) { feature ->
            val shape = RoundedCornerShape(nerdDimensions.cornerDefault)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(nerdColorPalette.surface)
            ) {
                FeatureItem(
                    name = feature.name,
                    isCategory = true,
                    isRefreshing = feature.isRefreshing,
                    onRefresh = { onRefreshFeature(feature.name) },
                )

                feature.gameList.forEach { game ->
                    FeatureItem(
                        name = game.name,
                        isCategory = false,
                        isRefreshing = game.isRefreshing,
                        onRefresh = { onRefreshGame(game.id) },
                        lastUpdatedTimeStamp = game.lastUpdatedTimeStamp,
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    name: String,
    isCategory: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    lastUpdatedTimeStamp: String? = null,
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
                text = name,
                style = if (isCategory) nerdTypography.titleLarge else nerdTypography.titleSmall,
                color = nerdColorPalette.textPrimary,
            )
            Spacer(Modifier.height(nerdDimensions.inlineGap))

            lastUpdatedTimeStamp?.let { timeStamp ->
                Text(
                    text = stringResource(Res.string.more_updates_settings_last_updated, timeStamp),
                    style = nerdTypography.titleSmall,
                    color = nerdColorPalette.textSecondary,
                )
            }
        }

        if (isRefreshing) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp),
            ) {
                CircularLoader(modifier = Modifier.size(24.dp))
            }
        } else {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = nerdColorPalette.textPrimary,
                )
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview
private fun PreviewEnabled() {
    FightingNerdTheme {
        Content(
            state = UpdatesState.PREVIEW_ENABLED,
            onExit = {},
            onToggleAutoUpdates = {},
            onSetPeriod = {},
            onSetUnit = {},
            onSave = {},
            onRefreshFeature = {},
            onRefreshGame = {},
        )
    }
}

@Composable
@Preview
private fun PreviewDisabled() {
    FightingNerdTheme {
        Content(
            state = UpdatesState.PREVIEW_DISABLED,
            onExit = {},
            onToggleAutoUpdates = {},
            onSetPeriod = {},
            onSetUnit = {},
            onSave = {},
            onRefreshFeature = {},
            onRefreshGame = {},
        )
    }
}
//endregion