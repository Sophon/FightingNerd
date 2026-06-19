package io.github.sophon.fightingnerd.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdTypography

@Composable
internal fun PlaceholderScreen(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(nerdColorPalette.background),
    ) {
        Text(
            text = label.uppercase(),
            style = nerdTypography.displayLarge,
            color = nerdColorPalette.textPrimary,
        )
    }
}
