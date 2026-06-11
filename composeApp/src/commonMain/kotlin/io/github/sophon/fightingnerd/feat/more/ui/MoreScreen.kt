package io.github.sophon.fightingnerd.feat.more.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.fightingnerd.BuildKonfig
import io.github.sophon.fightingnerd.LocalBottomBarPadding
import io.github.sophon.fightingnerd.core.ui.RoundedItem
import io.github.sophon.fightingnerd.feat.more.model.MoreItem
import io.github.sophon.fightingnerd.feat.more.model.Theme
import io.github.sophon.fightingnerd.feat.more.ui.theme.ThemeDialog
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MoreScreen(
    onItemClick: (MoreItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<MoreVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onItemClick = onItemClick,
        onThemeItemClick = vm::onThemeItemClick,
        onThemeSelected = vm::onThemeSelect,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: MoreState,
    onItemClick: (MoreItem) -> Unit,
    onThemeItemClick: (isDialogVisible: Boolean) -> Unit,
    onThemeSelected: (Theme) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(LocalBottomBarPadding.current),
        ) {
            ItemSection(onItemClick)

            Text(
                text = BuildKonfig.VERSION,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.isThemeSelectorVisible) {
            ThemeDialog(
                selectedTheme = Theme.System,
                onThemeSelected = onThemeSelected,
                onDismiss = { onThemeItemClick(false) },
            )
        }
    }
}

@Composable
private fun ItemSection(
    onItemClick: (MoreItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        itemsIndexed(MoreItem.entries) { index, moreItem ->
            val isLast = (index == MoreItem.entries.lastIndex)
            RoundedItem(
                isFirst = (index == 0),
                isLast = isLast,
                modifier = Modifier
                    .clickable { onItemClick(moreItem) }
                    .padding(horizontal = 8.dp),
            ) {
                Column {
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
                    if (isLast.not()) {
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun SettingsPreviewDark() {
    AppTheme(darkTheme = true) {
        Content(
            state = MoreState(),
            onItemClick = {},
            onThemeItemClick = {},
            onThemeSelected = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SettingsPreviewLight() {
    AppTheme(darkTheme = false) {
        Content(
            state = MoreState(),
            onItemClick = {},
            onThemeItemClick = {},
            onThemeSelected = {},
        )
    }
}
//endregion
