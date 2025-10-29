package com.example.cornerman.screens.moveList.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cornerman.screens.moveList.domain.MoveCategory
import com.example.cornerman.screens.moveList.ui.components.CategoriesBar
import com.example.cornerman.screens.moveList.ui.components.SearchBar
import com.example.cornerman.screens.moveList.ui.components.Section
import com.example.cornerman.theme.AppTheme
import com.example.cornerman.uiGallery.AppBottomBar
import com.example.cornerman.uiGallery.BottomBarItem
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MoveListScreen(
    charName: String,
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<MoveListVM>(
        parameters = { parametersOf(charName) }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onNotesExpandClick = vm::onExpandNotesFor,
        onStartSearch = vm::onStartSearch,
        onSearch = vm::onSearch,
        onSearchDone = vm::onSearchDone,
        onClearSearch = vm::onClearSearch,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: MoveListViewState,
    onNotesExpandClick: (String) -> Unit,
    onStartSearch: () -> Unit,
    onSearch: (String) -> Unit,
    onSearchDone: () -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCategoriesBarShown by remember { mutableStateOf(false) }
    val moveListState = rememberLazyListState()
    val categoriesBarState = rememberLazyListState()
    var targetCategoryIndex by remember { mutableStateOf<Int?>(null) }
    val scrollBasedIndex by remember {
        derivedStateOf { moveListState.firstVisibleItemIndex }
    }
    val currentCategoryIndex = targetCategoryIndex ?: scrollBasedIndex
    var isProgrammaticScroll by remember { mutableStateOf(false) }

    // Handle category click - scroll to that category
    val coroutineScope = rememberCoroutineScope()
    val onCategoryClick: (Int) -> Unit = { categoryIndex ->
        targetCategoryIndex = categoryIndex
        isProgrammaticScroll = true
        coroutineScope.launch {
            moveListState.animateScrollToItem(categoryIndex)
            isProgrammaticScroll = false
        }
    }

    LaunchedEffect(moveListState.isScrollInProgress) {
        if (moveListState.isScrollInProgress && isProgrammaticScroll.not()) targetCategoryIndex = null
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            //TODO: implement functions
            MoveListBottomBar(
                onHomeClick = {},
                onSaveClick = {},
                onSearchClick = onStartSearch,
                onContentsClick = {
                    isCategoriesBarShown = isCategoriesBarShown.not()
                },
            )
        },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) { paddingValues ->
        val focusManager = LocalFocusManager.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    onClick = {
                        focusManager.clearFocus()
                        onSearchDone()
                    }
                )
        ) {
            MoveList(
                movesByCategory = state.filteredMoves,
                expandedNotes = state.expandedNotesId,
                onNotesExpandClick = onNotesExpandClick,
                listState = moveListState,
            )

            if (isCategoriesBarShown) {
                CategoriesBar(
                    categories = state.filteredMoves,
                    onCategoryClick = onCategoryClick,
                    listState = categoriesBarState,
                    currentCategoryIndex = currentCategoryIndex,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp)
                )
            }

            state.searchBar?.let { searchBar ->
                SearchBar(
                    type = searchBar.type,
                    query = searchBar.query,
                    onSearch = onSearch,
                    onSearchDone = onSearchDone,
                    onClearSearch = onClearSearch,
                    focusManager = focusManager,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(92.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun MoveListBottomBar(
    onHomeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onSearchClick: () -> Unit,
    onContentsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBottomBar(
        items = listOf(
            BottomBarItem(
                icon = Icons.Outlined.Home,
                text = "Home",
                onClick = onHomeClick,
                isEnabled = false
            ),
            BottomBarItem(
                icon = Icons.Outlined.BookmarkBorder,
                text = "Save",
                onClick = onSaveClick,
                isEnabled = false,
            ),
            BottomBarItem(
                icon = Icons.Outlined.Search,
                text = "Search",
                onClick = onSearchClick,
            ),
            BottomBarItem(
                icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                text = "Contents",
                onClick = onContentsClick,
            )
        ),
        modifier = modifier,
    )
}

@Composable
private fun MoveList(
    movesByCategory: List<MoveCategory>,
    expandedNotes: Set<String>,
    onNotesExpandClick: (String) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
    ) {
        items(
            items = movesByCategory,
            key = { moveCategory -> moveCategory.name }
        ) { moveCategory ->
            Section(
                title = moveCategory.name,
                moves = moveCategory.moves,
                expandedNotes = expandedNotes,
                onNotesExpandClick = onNotesExpandClick,
                modifier = Modifier
                    .padding(4.dp)
            )
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun MoveListPreviewDark() {
    val state = MoveListViewState.PREVIEW
    AppTheme(darkTheme = true) {
        Content(
            state = state,
            onNotesExpandClick = {},
            onStartSearch = {},
            onSearch = {},
            onSearchDone = {},
            onClearSearch = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MoveListPreviewLight() {
    val state = MoveListViewState.PREVIEW
    AppTheme(darkTheme = false) {
        Content(
            state = state,
            onNotesExpandClick = {},
            onStartSearch = {},
            onSearch = {},
            onSearchDone = {},
            onClearSearch = {},
        )
    }
}
//endregion