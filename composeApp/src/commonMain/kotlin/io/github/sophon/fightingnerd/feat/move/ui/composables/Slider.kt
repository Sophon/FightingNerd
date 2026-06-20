package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpSize
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.move_list_filter_max
import fightingnerd.composeapp.generated.resources.move_list_filter_min
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Slider(
    label: String,
    min: Int,
    max: Int,
    value: MoveListState.FilterSheet.MinMax?,
    onChange: (MoveListState.FilterSheet.MinMax?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sliderMin = min - 1
    val sliderMax = max + 1

    val onChangeNormalized: (MoveListState.FilterSheet.MinMax?) -> Unit = { raw ->
        val normalized = raw?.let {
            val newMin = if (it.min != null && it.min <= sliderMin) null else it.min
            val newMax = if (it.max != null && it.max >= sliderMax) null else it.max
            if (newMin == null && newMax == null) {
                null
            } else {
                MoveListState.FilterSheet.MinMax(min = newMin, max = newMax)
            }
        }
        onChange(normalized)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(nerdDimensions.componentPadding),
    ) {
        Text(
            text = label,
            color = nerdColorPalette.textSecondary,
            style = nerdTypography.titleMedium,
        )
        Spacer(Modifier.height(nerdDimensions.componentPaddingTight))

        SliderSection(
            sliderMin = sliderMin,
            sliderMax = sliderMax,
            value = value,
            onChange = onChangeNormalized,
        )

        Spacer(Modifier.height(nerdDimensions.componentPaddingTight))

        NumericInputSection(
            sliderMin = sliderMin,
            sliderMax = sliderMax,
            value = value,
            onChange = onChangeNormalized,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SliderSection(
    sliderMin: Int,
    sliderMax: Int,
    value: MoveListState.FilterSheet.MinMax?,
    onChange: (MoveListState.FilterSheet.MinMax?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val effectiveMin = value?.min ?: sliderMin
    val effectiveMax = value?.max ?: sliderMax

    val rangeSliderState = remember(sliderMin, sliderMax) {
        RangeSliderState(
            activeRangeStart = effectiveMin.toFloat(),
            activeRangeEnd = effectiveMax.toFloat(),
            valueRange = sliderMin.toFloat()..sliderMax.toFloat(),
        )
    }

    LaunchedEffect(effectiveMin, effectiveMax) {
        rangeSliderState.activeRangeStart = effectiveMin.toFloat()
        rangeSliderState.activeRangeEnd = effectiveMax.toFloat()
    }

    LaunchedEffect(rangeSliderState) {
        snapshotFlow { rangeSliderState.activeRangeStart to rangeSliderState.activeRangeEnd }
            .collect { (start, end) ->
                val newMin = start.roundToInt()
                val newMax = end.roundToInt()
                if (newMin != effectiveMin || newMax != effectiveMax) {
                    onChange(
                        MoveListState.FilterSheet.MinMax(
                            min = newMin,
                            max = newMax,
                        )
                    )
                }
            }
    }

    val inactiveColor = nerdColorPalette.divider
    val accentColor = nerdColorPalette.accent
    val minThumbColor = if (value?.min != null) accentColor else inactiveColor
    val maxThumbColor = if (value?.max != null) accentColor else inactiveColor
    val trackColor = if (value != null) accentColor else inactiveColor

    val minThumbColors = SliderDefaults.colors(thumbColor = minThumbColor)
    val maxThumbColors = SliderDefaults.colors(thumbColor = maxThumbColor)
    val trackColors = SliderDefaults.colors(
        activeTrackColor = trackColor,
        inactiveTrackColor = nerdColorPalette.dividerSubtle,
    )

    val startInteractionSource = remember { MutableInteractionSource() }
    val endInteractionSource = remember { MutableInteractionSource() }

    RangeSlider(
        state = rangeSliderState,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        startThumb = {
            SliderDefaults.Thumb(
                interactionSource = startInteractionSource,
                colors = minThumbColors,
                thumbSize = DpSize(width = nerdDimensions.iconDefault, height = nerdDimensions.iconDefault),
            )
        },
        endThumb = {
            SliderDefaults.Thumb(
                interactionSource = endInteractionSource,
                colors = maxThumbColors,
                thumbSize = DpSize(width = nerdDimensions.iconDefault, height =nerdDimensions.iconDefault),
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                rangeSliderState = sliderState,
                colors = trackColors,
                drawStopIndicator = null,
                drawTick = { _, _ -> },
            )
        },
        modifier = modifier,
    )
}

@Composable
private fun NumericInputSection(
    sliderMin: Int,
    sliderMax: Int,
    value: MoveListState.FilterSheet.MinMax?,
    onChange: (MoveListState.FilterSheet.MinMax?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        NumberField(
            value = value?.min,
            hint = stringResource(Res.string.move_list_filter_min),
            onValueChange = { newMin ->
                onChange(
                    value?.copy(min = newMin) ?: MoveListState.FilterSheet.MinMax(min = newMin)
                )
            },
            range = sliderMin..sliderMax,
            modifier = Modifier.weight(1f),
        )

        IconButton(
            onClick = { onChange(null) },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = nerdColorPalette.textSecondary,
            ),
            modifier = Modifier
                .size(nerdDimensions.iconLarge)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                tint = nerdColorPalette.textPrimary,
                contentDescription = null,
            )
        }

        NumberField(
            value = value?.max,
            hint = stringResource(Res.string.move_list_filter_max),
            onValueChange = { newMax ->
                onChange(
                    value?.copy(max = newMax) ?: MoveListState.FilterSheet.MinMax(max = newMax)
                )
            },
            range = sliderMin..sliderMax,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun NumberField(
    value: Int?,
    hint: String,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier,
) {
    var fieldValue by remember {
        mutableStateOf(TextFieldValue(text = value?.toString().orEmpty()))
    }
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(value) {
        if (fieldValue.text.toIntOrNull() != value) {
            fieldValue = TextFieldValue(text = value?.toString().orEmpty())
        }
    }

    LaunchedEffect(isFocused) {
        if (isFocused && fieldValue.text.isNotEmpty()) {
            withFrameNanos { }
            fieldValue = fieldValue.copy(
                selection = TextRange(0, fieldValue.text.length)
            )
        } else if (isFocused.not()) {
            keyboardController?.hide()
        }
    }

    TextField(
        value = fieldValue,
        onValueChange = { new ->
            fieldValue = new
            val parsed = new.text.toIntOrNull()
            if (parsed != null && parsed in range) {
                onValueChange(parsed)
            }
        },
        placeholder = { Text(text = hint) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier.onFocusChanged {
            isFocused = it.isFocused
        }
    )
}


//region PREVIEW
@Preview
@Composable
private fun SliderPreview_NoInput() {
    FightingNerdTheme {
        Slider(
            label = "Startup",
            min = 5,
            max = 40,
            value = null,
            onChange = {},
        )
    }
}

@Preview
@Composable
private fun SliderPreview_Adjusted() {
    FightingNerdTheme {
        Slider(
            label = "Startup",
            min = 5,
            max = 40,
            value = MoveListState.FilterSheet.MinMax(min = 10, max = 25),
            onChange = {},
        )
    }
}

@Preview
@Composable
private fun SliderPreview_MinOnly() {
    FightingNerdTheme {
        Slider(
            label = "Startup",
            min = 5,
            max = 40,
            value = MoveListState.FilterSheet.MinMax(min = 10, max = null),
            onChange = {},
        )
    }
}
//endregion
