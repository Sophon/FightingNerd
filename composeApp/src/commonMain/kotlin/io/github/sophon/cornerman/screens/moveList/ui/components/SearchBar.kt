package io.github.sophon.cornerman.screens.moveList.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import io.github.sophon.cornerman.screens.moveList.ui.MoveListViewState
import io.github.sophon.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchBar(
    type: MoveListViewState.SearchBar.Type,
    query: String,
    onSearch: (String) -> Unit,
    onSearchDone: () -> Unit,
    onClearSearch: () -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier,
) {
    when (type) {
        MoveListViewState.SearchBar.Type.FIELD -> {
            FieldSearchBar(
                query = query,
                onSearch = onSearch,
                onSearchDone = onSearchDone,
                onClearSearch = onClearSearch,
                focusManager = focusManager,
                modifier = modifier,
            )
        }
        MoveListViewState.SearchBar.Type.CHIP -> {
            SearchChip(
                query = query,
                onClearSearch = onClearSearch,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldSearchBar(
    query: String,
    onSearch: (String) -> Unit,
    onSearchDone: () -> Unit,
    onClearSearch: () -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onSearch,
                onSearch = {
                    focusManager.clearFocus()
                    onSearchDone()
                },
                expanded = false,
                onExpandedChange = {},
                leadingIcon = {
                    Icon(imageVector = Icons.TwoTone.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(imageVector = Icons.TwoTone.Clear, contentDescription = "Clear search")
                        }
                    }
                }
            )
        },
        expanded = false,
        onExpandedChange = {},
        modifier = modifier,
    ) {}
}

@Composable
private fun SearchChip(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 3.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        ) {
            Text(
                text = query,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            VerticalDivider(
                modifier = Modifier.height(24.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            IconButton(
                onClick = onClearSearch,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun FieldSearchBarPreviewDark() {
    AppTheme(darkTheme = true) {
        FieldSearchBar(
            query = "Something",
            onSearch = {},
            onSearchDone = {},
            onClearSearch = {},
            focusManager = LocalFocusManager.current,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun FieldSearchBarPreviewLight() {
    AppTheme(darkTheme = false) {
        FieldSearchBar(
            query = "",
            onSearch = {},
            onSearchDone = {},
            onClearSearch = {},
            focusManager = LocalFocusManager.current,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SearchChipPreviewDark() {
    AppTheme(true) {
        SearchChip(
            query = "query",
            onClearSearch = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SearchChipPreviewLight() {
    AppTheme(false) {
        SearchChip(
            query = "query",
            onClearSearch = {},
        )
    }
}
//endregion