package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.move_list_search_hint
import io.github.sophon.fightingnerd.core.ui.components.TopBarButton
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MoveTopBar(
    onExit: () -> Unit,
    characterName: String,
    searchQuery: String?,
    onSearch: (query: String?) -> Unit,
    onDisplayFilterSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = nerdDimensions.screenPaddingHorizontal,
                vertical = nerdDimensions.screenPaddingVertical,
            )
    ) {
        TopBarButton(onClick = onExit)

        if (searchQuery == null) {
            Text(
                text = characterName.uppercase(),
                style = nerdTypography.displaySmall,
                color = nerdColorPalette.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        } else {
            SearchField(
                query = searchQuery,
                onQueryChange = onSearch,
                modifier = Modifier.weight(1f),
            )
        }

        ButtonsRow(
            searchQuery = searchQuery,
            onShowSearch = { onSearch("") },
            onDisplayFilterSheet = onDisplayFilterSheet,
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(nerdColorPalette.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        BasicTextField(
            value = query,
            onValueChange = { onQueryChange(it) },
            textStyle = nerdTypography.bodyMedium.copy(color = nerdColorPalette.textPrimary),
            cursorBrush = SolidColor(nerdColorPalette.textPrimary),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.move_list_search_hint).uppercase(),
                            style = nerdTypography.bodyMedium,
                            color = nerdColorPalette.textSecondary,
                        )
                    }
                    innerTextField()
                }
            },
        )

        IconButton(
            onClick = { onQueryChange(if (query.isEmpty()) null else "") },
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = if (query.isEmpty()) "Close search" else "Clear search",
                tint = nerdColorPalette.textPrimary,
            )
        }
    }
}

@Composable
private fun ButtonsRow(
    searchQuery: String?,
    onShowSearch: () -> Unit,
    onDisplayFilterSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (searchQuery == null) {
            IconButton(onClick = onShowSearch) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "search",
                    tint = nerdColorPalette.textPrimary,
                )
            }
        }

        IconButton(onClick = onDisplayFilterSheet,) {
            Icon(
                imageVector = Icons.Outlined.FilterList,
                contentDescription = "Filter moves",
                tint = nerdColorPalette.textPrimary,
            )
        }
    }
}
