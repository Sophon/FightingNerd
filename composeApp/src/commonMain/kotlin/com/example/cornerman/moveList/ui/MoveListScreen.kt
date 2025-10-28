package com.example.cornerman.moveList.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var categoriesSheetIsShown by remember { mutableStateOf(true) }

    Scaffold(
        floatingActionButton = {
            FAB(
                onClick = { categoriesSheetIsShown = true },
                categories = state.movesByCategory.map { it.name },
                onDismiss = { categoriesSheetIsShown = false },
            )
        },
        bottomBar = {
            MoveListBottomBar()
        },
        modifier = modifier,
    ) { paddingValues ->
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

@Composable
private fun FAB(
    onClick: () -> Unit,
    categories: List<String>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        CategoriesBar(
            categories = categories,
            onCategoryClick = {},
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FloatingActionButton(
            onClick = onClick,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.FormatListBulleted,
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriesBar(
    categories: List<String>,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(.5f),
                shape = RoundedCornerShape(4.dp),
            ),
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(bottom = 16.dp, start = 2.dp, end = 2.dp)
        ) {
            categories.forEachIndexed { index, category ->
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCategoryClick(index) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
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