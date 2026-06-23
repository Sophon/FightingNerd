package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions

@Composable
internal fun TopBarButton(
    onClick: () -> Unit,
    imageVector: ImageVector = Icons.AutoMirrored.Outlined.ArrowBack,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(nerdDimensions.iconLarge)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = nerdColorPalette.textPrimary,
        )
    }
}
