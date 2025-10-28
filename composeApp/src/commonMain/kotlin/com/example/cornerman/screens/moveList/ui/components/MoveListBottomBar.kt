package com.example.cornerman.screens.moveList.ui.components

import androidx.compose.foundation.background
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
import com.example.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MoveListBottomBar(
    onContentsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 4.dp, horizontal = 8.dp),
    ) {
        BottomBarItem(
            icon = Icons.Outlined.Home,
            text = "Home",
            onClick = {},
        )

        BottomBarItem(
            icon = Icons.Outlined.BookmarkBorder, //TODO: should be BookmarkAdd or BookmarkRemove
            text = "Save",
            onClick = {},
        )

        BottomBarItem(
            icon = Icons.Outlined.Search,
            text = "Search",
            onClick = {},
        )

        BottomBarItem(
            icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
            text = "Contents",
            onClick = onContentsClick,
        )
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick),
    ) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
        )
        Spacer(Modifier.height(2.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun BottomBarPreviewDark() {
    AppTheme(darkTheme = true) {
        MoveListBottomBar(
            onContentsClick = {},
        )
    }
}
@Composable
@Preview(showBackground = true)
private fun BottomBarPreviewLight() {
    AppTheme(darkTheme = false) {
        MoveListBottomBar(
            onContentsClick = {},
        )
    }
}
//endregion