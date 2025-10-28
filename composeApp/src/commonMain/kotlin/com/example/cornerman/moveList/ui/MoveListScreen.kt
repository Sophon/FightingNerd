package com.example.cornerman.moveList.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cornerman.moveList.model.MoveCategory
import com.example.cornerman.moveList.ui.components.MoveListBottomBar
import com.example.cornerman.moveList.ui.components.Section
import com.example.cornerman.theme.AppTheme
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
    var isCategoriesBarShown by remember { mutableStateOf(true) }

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
//            .background(Color.Red)
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
            )

            //TODO: contents sidebar
        }
    }
}

@Composable
private fun MoveList(
    movesByCategory: List<MoveCategory>,
    expandedNotes: Set<String>,
    onNotesExpandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
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
                    .clip(RoundedCornerShape(8.dp))
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