package io.github.sophon.fightingnerd.feat.more.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.sophon.fightingnerd.core.ui.RoundedItem
import io.github.sophon.fightingnerd.feat.home.ui.MoreItem
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MoreScreen(
    onItemClick: (MoreItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        itemsIndexed(MoreItem.entries) { index, moreItem ->
            RoundedItem(
                isFirst = index == 0,
                isLast = index == MoreItem.entries.lastIndex,
                modifier = Modifier.clickable { onItemClick(moreItem) },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Text(
                        text = stringResource(moreItem.stringResource),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun SettingsPreviewDark() {
    AppTheme(darkTheme = true) {
        MoreScreen(
            onItemClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SettingsPreviewLight() {
    AppTheme(darkTheme = false) {
        MoreScreen(
            onItemClick = {},
        )
    }
}
//endregion
