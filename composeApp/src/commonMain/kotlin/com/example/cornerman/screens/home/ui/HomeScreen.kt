package com.example.cornerman.screens.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cornerman.theme.AppTheme
import com.example.cornerman.uiGallery.AppBottomBar
import com.example.cornerman.uiGallery.BottomBarItem
import com.example.wikiwavu.domain.model.Character
import cornerman.composeapp.generated.resources.Res
import cornerman.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<HomeVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onCharacterClick = onCharacterClick,
        onSavedClick = vm::onSavedClick,
        onSearchClick = vm::onSearchClick,
        onSettingsClick = vm::onSettingsClick,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: HomeViewState,
    onCharacterClick: (String) -> Unit,
    onSavedClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            HomeBottomBar(
                onSavedClick = onSavedClick,
                onSearchClick = onSearchClick,
                onSettingsClick = onSettingsClick,
            )
        },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) { paddingValues ->
        val focusManager = LocalFocusManager.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    onClick = {
                        focusManager.clearFocus()
//                        onSearchDone()
                    }
                )
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(100.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterHorizontally,
                ),
                verticalItemSpacing = 4.dp,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                itemsIndexed(state.characterList) { index, character ->
                    CharacterPanel(
                        character = character,
                        onClick = { onCharacterClick(character.name) },
                    )
                }
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
private fun CharacterPanel(
    character: Character,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .width(100.dp)
            .height(128.dp)
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = character.portraitUrl,
            contentDescription = character.name,
            placeholder = painterResource(Res.drawable.compose_multiplatform),
            error = painterResource(Res.drawable.compose_multiplatform),
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = character.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun HomeBottomBar(
    onSavedClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBottomBar(
        items = listOf(
            BottomBarItem(
                icon = Icons.Outlined.Bookmarks,
                text = "Saved",
                onClick = onSavedClick,
                isEnabled = false,
            ),
            BottomBarItem(
                icon = Icons.Outlined.Search,
                text = "Search",
                onClick = onSearchClick,
                isEnabled = false,
            ),
            BottomBarItem(
                icon = Icons.Outlined.Settings,
                text = "Settings",
                onClick = onSettingsClick,
                isEnabled = false,
            )
        ),
        modifier = modifier,
    )
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun HomeScreenPreviewDark() {
    AppTheme(darkTheme = true) {
        Content(
            state = HomeViewState.PREVIEW,
            onCharacterClick = {},
            onSavedClick = {},
            onSearchClick = {},
            onSettingsClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun HomeScreenPreviewLight() {
    AppTheme(darkTheme = false) {
        Content(
            state = HomeViewState.PREVIEW,
            onCharacterClick = {},
            onSavedClick = {},
            onSearchClick = {},
            onSettingsClick = {},
        )
    }
}
//endregion