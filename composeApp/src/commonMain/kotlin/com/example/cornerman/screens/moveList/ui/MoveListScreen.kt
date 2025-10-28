package com.example.cornerman.screens.moveList.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cornerman.screens.moveList.domain.MoveCategory
import com.example.cornerman.screens.moveList.ui.components.CategoriesBar
import com.example.cornerman.screens.moveList.ui.components.MoveListBottomBar
import com.example.cornerman.screens.moveList.ui.components.Section
import com.example.cornerman.theme.AppTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoveListScreen(
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<MoveListVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onNotesExpandClick = vm::onExpandNotesFor,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: MoveListViewState,
    onNotesExpandClick: (String) -> Unit,
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
            MoveListBottomBar(
                onContentsClick = {
                    isCategoriesBarShown = isCategoriesBarShown.not()
                }
            )
        },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MoveList(
                movesByCategory = state.movesByCategory,
                expandedNotes = state.expandedNotesId,
                onNotesExpandClick = onNotesExpandClick,
                listState = moveListState,
            )

            if (isCategoriesBarShown) {
                CategoriesBar(
                    categories = state.movesByCategory,
                    onCategoryClick = onCategoryClick,
                    listState = categoriesBarState,
                    currentCategoryIndex = currentCategoryIndex,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp)
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
        items(movesByCategory) { moveCategory ->
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
        )
    }
}
//endregion