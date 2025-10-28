package com.example.cornerman.moveList.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp)
    )
}

@Composable
private fun Content(
    state: MoveListViewState,
    onNotesExpandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Navigate to section */ }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.FormatListBulleted,
                    contentDescription = null,
                )
            }
        },
        bottomBar = {
            MoveListBottomBar()
        },
        modifier = modifier,
    ) {
        MoveList(
            movesByCategory = state.movesByCategory,
            expandedNotes = state.expandedNotesId,
            onNotesExpandClick = onNotesExpandClick,
        )
    }
}

@Composable
private fun MoveList(
    movesByCategory: List<MoveCategory>,
    expandedNotes: Set<String>,
    onNotesExpandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        movesByCategory.forEach { moveCategory ->
            Section(
                title = moveCategory.name,
                moves = moveCategory.moves,
                expandedNotes = expandedNotes,
                onNotesExpandClick = onNotesExpandClick,
            )
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun MoveListPreview() {
    val state = MoveListViewState.PREVIEW
    AppTheme {
        Content(
            state = state,
            onNotesExpandClick = {},
        )
    }
}
//endregion