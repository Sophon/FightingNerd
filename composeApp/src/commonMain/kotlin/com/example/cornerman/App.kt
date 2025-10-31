package com.example.cornerman

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.cornerman.screens.home.ui.HomeScreen
import com.example.cornerman.screens.moveList.ui.MoveListScreen
import com.example.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
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
                )
            }
        }
    }
}