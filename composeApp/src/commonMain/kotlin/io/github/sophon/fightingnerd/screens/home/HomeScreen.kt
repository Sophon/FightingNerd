package io.github.sophon.fightingnerd.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.sophon.fightingnerd.Destination
import io.github.sophon.fightingnerd.uiGallery.AppBottomBar
import io.github.sophon.fightingnerd.uiGallery.BottomBarItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<HomeVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            HomeBottomBar(
                onSavedClick = vm::onSavedClick,
                onSearchClick = vm::onSearchClick,
                onSettingsClick = { navHostController.navigate(Destination.Settings) },
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
                    }
                )
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.Top,
                ),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.composeRegisteredFeatures) { registeredFeature ->
                    registeredFeature.HomeScreenContent(navHostController)
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
                isEnabled = true,
            )
        ),
        modifier = modifier,
    )
}
