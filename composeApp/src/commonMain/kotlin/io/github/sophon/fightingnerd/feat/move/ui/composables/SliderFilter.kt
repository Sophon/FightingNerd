package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.move_list_filter_max
import fightingnerd.composeapp.generated.resources.move_list_filter_min
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SliderFilter(
    label: String,
    min: Int,
    max: Int,
    value: MoveListState.FilterSheet.MinMax?,
    onChange: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onDraggingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(nerdDimensions.componentPadding),
    ) {
        Text(
            text = label,
            color = nerdColorPalette.textPrimary,
            style = nerdTypography.titleMedium,
        )
        Spacer(Modifier.height(nerdDimensions.componentPaddingTight))

        val sliderMin = min - 1
        val sliderMax = max + 1
        SliderSection(
            sliderMin = sliderMin,
            sliderMax = sliderMax,
            value = value,
            onChange = onChange,
            onDraggingChange = onDraggingChange,
        )

        Spacer(Modifier.height(nerdDimensions.componentPaddingTight))

        NumericInputSection(
            value = value,
            onChange = onChange,
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
    onDraggingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rangeSliderState = remember(sliderMin, sliderMax) {
        RangeSliderState(
            activeRangeStart = value?.min?.toFloat() ?: sliderMin.toFloat(),
            activeRangeEnd = value?.max?.toFloat() ?: sliderMax.toFloat(),
            valueRange = sliderMin.toFloat()..sliderMax.toFloat(),
        )
    }

    LaunchedEffect(rangeSliderState) {
        snapshotFlow { rangeSliderState.activeRangeStart to rangeSliderState.activeRangeEnd }
            .collect { (start, end) ->
                onChange(
                    MoveListState.FilterSheet.MinMax(
                        min = start.roundToInt(),
                        max = end.roundToInt(),
                    )
                )
            }
    }

    LaunchedEffect(value) {
        val targetMin = value?.min?.toFloat() ?: sliderMin.toFloat()
        val targetMax = value?.max?.toFloat() ?: sliderMax.toFloat()

        if (rangeSliderState.activeRangeStart.roundToInt() != targetMin.roundToInt()) {
            rangeSliderState.activeRangeStart = targetMin
        }

        if (rangeSliderState.activeRangeEnd.roundToInt() != targetMax.roundToInt()) {
            rangeSliderState.activeRangeEnd = targetMax
        }
    }

    val trackColor = if (value == null) nerdColorPalette.divider else nerdColorPalette.accent
    val trackColors = SliderDefaults.colors(
        activeTrackColor = trackColor,
        inactiveTrackColor = nerdColorPalette.dividerSubtle,
    )
    val startInteractionSource = remember { MutableInteractionSource() }
    val endInteractionSource = remember { MutableInteractionSource() }
    LaunchedEffect(startInteractionSource, endInteractionSource) {
        merge(
            startInteractionSource.interactions,
            endInteractionSource.interactions,
        ).collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> onDraggingChange(true)
                is DragInteraction.Stop,
                is DragInteraction.Cancel -> onDraggingChange(false)
            }
        }
    }
    RangeSlider(
        state = rangeSliderState,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        startThumb = { CircleThumb(value?.min) },
        endThumb = { CircleThumb(value?.max) },
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
private fun CircleThumb(
    value: Int?,
    modifier: Modifier = Modifier
) {
    val color = if (value == null) nerdColorPalette.divider else nerdColorPalette.accent
    Box(
        modifier = modifier
            .size(nerdDimensions.iconDefault)
            .background(color = color, shape = CircleShape),
    )
}

@Composable
private fun NumericInputSection(
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
                imageVector = Icons.Outlined.Delete,
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
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun NumberField(
    value: Int?,
    hint: String,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var fieldValue by remember {
        mutableStateOf(TextFieldValue(text = value?.toString().orEmpty()))
    }
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

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

    LaunchedEffect(value, isFocused) {
        if (isFocused.not() && fieldValue.text.toIntOrNull() != value) {
            fieldValue = TextFieldValue(text = value?.toString().orEmpty())
        }
    }

    TextField(
        value = fieldValue,
        onValueChange = { new ->
            fieldValue = new
            val parsed = new.text.toIntOrNull()
            if (parsed != null) {
                onValueChange(parsed)
            }
        },
        placeholder = { Text(text = hint) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RectangleShape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = nerdColorPalette.surfaceHigh,
            unfocusedContainerColor = nerdColorPalette.surfaceHigh,
            disabledContainerColor = nerdColorPalette.surfaceHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
    )
}


//region PREVIEW
@Preview
@Composable
private fun SliderPreview_NoInput() {
    FightingNerdTheme {
        SliderFilter(
            label = "Startup",
            min = 5,
            max = 40,
            value = null,
            onChange = {},
            onDraggingChange = {},
        )
    }
}

@Preview
@Composable
private fun SliderPreview_Adjusted() {
    FightingNerdTheme {
        SliderFilter(
            label = "Startup",
            min = 5,
            max = 40,
            value = MoveListState.FilterSheet.MinMax(min = 10, max = 25),
            onChange = {},
            onDraggingChange = {},
        )
    }
}

@Preview
@Composable
private fun SliderPreview_MinOnly() {
    FightingNerdTheme {
        SliderFilter(
            label = "Startup",
            min = 5,
            max = 40,
            value = MoveListState.FilterSheet.MinMax(min = 10, max = null),
            onChange = {},
            onDraggingChange = {},
        )
    }
}
//endregion
