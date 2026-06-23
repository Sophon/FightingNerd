package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ToastSnackbar(
    toast: Toast,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when (toast.type) {
        Toast.Type.INFO -> nerdColorPalette.surfaceHigh
        Toast.Type.WARNING -> nerdColorPalette.warning
        Toast.Type.ERROR -> nerdColorPalette.error
    }
    val textColor = when (toast.type) {
        Toast.Type.INFO -> nerdColorPalette.textPrimary
        Toast.Type.WARNING -> nerdColorPalette.background
        Toast.Type.ERROR -> nerdColorPalette.background
    }
    val icon = when (toast.type) {
        Toast.Type.INFO -> Icons.Outlined.Info
        Toast.Type.WARNING -> Icons.Outlined.Warning
        Toast.Type.ERROR -> Icons.Outlined.Error
    }

    Row(
        modifier = modifier
            .padding(horizontal = nerdDimensions.screenPaddingHorizontal)
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .background(backgroundColor)
            .padding(
                horizontal = nerdDimensions.componentPadding,
                vertical = nerdDimensions.componentPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
        )
        Spacer(modifier = Modifier.width(nerdDimensions.componentPadding))

        Text(
            text = toast.message,
            color = textColor,
            style = nerdTypography.labelLarge,
        )
    }
}

internal data class ToastVisuals(val toast: Toast) : SnackbarVisuals {
    override val message: String = toast.message
    override val actionLabel: String? = null
    override val duration: SnackbarDuration = SnackbarDuration.Short
    override val withDismissAction: Boolean = false
}


//region PREVIEW
@Preview
@Composable
private fun ToastSnackbarInfoPreview() {
    FightingNerdTheme {
        ToastSnackbar(
            toast = Toast(
                message = "Profile saved",
                type = Toast.Type.INFO,
            ),
        )
    }
}

@Preview
@Composable
private fun ToastSnackbarWarningPreview() {
    FightingNerdTheme {
        ToastSnackbar(
            toast = Toast(
                message = "Connection unstable",
                type = Toast.Type.WARNING,
            ),
        )
    }
}

@Preview
@Composable
private fun ToastSnackbarErrorPreview() {
    FightingNerdTheme {
        ToastSnackbar(
            toast = Toast(
                message = "Failed to load move data",
                type = Toast.Type.ERROR,
            ),
        )
    }
}
//endregion