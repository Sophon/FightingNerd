package io.github.sophon.fightingnerd.feat.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.sophon.fightingnerd.feat.home.ui.composables.CharacterRow
import io.github.sophon.fightingnerd.feat.home.ui.composables.Widget
import io.github.sophon.fightingnerd.feat.home.ui.composables.WidgetHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<HomeVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.Top,
                ),
                modifier = Modifier.fillMaxSize(),
            ) {
//                items(state.gameWidgetList) { gameWidget ->
//                    Widget(
//                        widget = gameWidget,
//                        onExpandWidget = vm::onExpandWidget,
//                    )
//                }

                state.gameWidgetList.forEach { widget ->
                    item(key = "header_${widget.game.id}") {
                        WidgetHeader(
                            game = widget.game,
                            featureName = widget.featureName,
                            isExpanded = widget.isExpanded,
                            onExpandClick = vm::onExpandWidget,
                            isLoading = widget.isLoading,
                        )
                    }

                    if (widget.isExpanded) {
                        val rows = widget.characterList.chunked(4) //TODO: calculate columns
                        items(
                            items = rows,
                            key = { row -> "row_${widget.game.id}_${row.first().id}" },
                        ) { rowCharacters ->
                            CharacterRow(
                                characterList = rowCharacters,
                                onCharacterClick = {},
                            )
                        }
                    }
                }
            }
        }
    }
}
