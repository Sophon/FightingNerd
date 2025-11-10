package io.github.sophon.cornerman

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.sophon.cornerman.featureRegistry.FeatureRegistry
import io.github.sophon.cornerman.screens.home.HomeScreen
import io.github.sophon.cornerman.screens.moveList.ui.MoveListScreen
import io.github.sophon.cornerman.screens.settings.ui.SettingsScreen
import io.github.sophon.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val featureRegistry = koinInject<FeatureRegistry>()
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        featureRegistry.initialize()
        isInitialized = true
    }

    if (isInitialized) {
        AppTheme {
            val navHostController = rememberNavController()

            NavHost(
                navController = navHostController,
                startDestination = Destination.Home,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable<Destination.Home> {
                    HomeScreen(navHostController)
                }

                composable<Destination.MoveList> { navBackstackEntry ->
                    MoveListScreen(
                        charName = navBackstackEntry.toRoute<Destination.MoveList>().charName,
                        wikiQualifier = navBackstackEntry.toRoute<Destination.MoveList>().wikiQualifier,
                    )
                }

                composable<Destination.Settings> {
                    SettingsScreen()
                }
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