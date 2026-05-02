package io.github.sophon.fightingnerd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.sophon.fightingnerd.feat.bottomBar.ui.BottomBarView
import io.github.sophon.fightingnerd.featureRegistry.FeatureRegistry
import io.github.sophon.fightingnerd.screens.home.HomeScreen
import io.github.sophon.fightingnerd.screens.moveList.ui.MoveListScreen
import io.github.sophon.fightingnerd.screens.settings.ui.SettingsScreen
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
internal fun App() {
    val featureRegistry = koinInject<FeatureRegistry>()
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        featureRegistry.initialize()
        isInitialized = true
    }

    if (isInitialized) {
        AppTheme {
            val navHostController = rememberNavController()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                NavHost(
                    navController = navHostController,
                    startDestination = Destination.Home,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    composable<Destination.Home> {
                        HomeScreen(navHostController)
                    }

                    composable<Destination.MoveList> { navBackstackEntry ->
                        val route = navBackstackEntry.toRoute<Destination.MoveList>()
                        MoveListScreen(
                            gameId = route.gameId,
                            charName = route.charName,
                        )
                    }

                    composable<Destination.Settings> {
                        SettingsScreen()
                    }
                }

                BottomBarView(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}