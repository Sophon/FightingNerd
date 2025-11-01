package io.github.sophon.cornerman.uiGallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.sophon.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

data class BottomBarItem(
    val icon: ImageVector,
    val onClick: () -> Unit,
    val text: String? = null,
    val isEnabled: Boolean = true,
)

@Composable
fun AppBottomBar(
    items: List<BottomBarItem>,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp)
            .border(
                width = .5.dp,
                color = MaterialTheme.colorScheme.outline.copy(.1f),
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
    ) {
        items.forEach { item ->
            BottomBarItem(
                icon = item.icon,
                text = item.text,
                isEnabled = item.isEnabled,
                onClick = item.onClick,
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    text: String?,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(enabled = isEnabled, onClick = onClick),
    ) {
        val alpha = if (isEnabled) 1f else .5f

        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
        )
        Spacer(Modifier.height(2.dp))

        Text(
            text = text.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
        )
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun BottomBarPreviewDark() {
    AppTheme(darkTheme = true) {
        AppBottomBar(
            items = listOf(
                BottomBarItem(
                    icon = Icons.Outlined.Home,
                    text = "Home",
                    onClick = {},
                    isEnabled = false
                ),
                BottomBarItem(
                    icon = Icons.Outlined.BookmarkBorder,
                    text = "Save",
                    onClick = {},
                    isEnabled = false,
                ),
                BottomBarItem(
                    icon = Icons.Outlined.Search,
                    text = "Search",
                    onClick = {},
                ),
                BottomBarItem(
                    icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                    text = "Contents",
                    onClick = {},
                )
            )
        )
    }
}
@Composable
@Preview(showBackground = true)
private fun BottomBarPreviewLight() {
    AppTheme(darkTheme = false) {
        AppBottomBar(
            items = listOf(
                BottomBarItem(
                    icon = Icons.Outlined.Home,
                    text = "Home",
                    onClick = {},
                    isEnabled = false
                ),
                BottomBarItem(
                    icon = Icons.Outlined.BookmarkBorder,
                    text = "Save",
                    onClick = {},
                    isEnabled = false,
                ),
                BottomBarItem(
                    icon = Icons.Outlined.Search,
                    text = "Search",
                    onClick = {},
                ),
                BottomBarItem(
                    icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                    text = "Contents",
                    onClick = {},
                )
            )
        )
    }
}
//endregion