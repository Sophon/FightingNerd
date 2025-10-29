package com.example.cornerman

import androidx.compose.runtime.Composable
import com.example.cornerman.screens.home.ui.HomeScreen
import com.example.cornerman.screens.moveList.ui.MoveListScreen
import com.example.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {
//        MoveListScreen()
        HomeScreen()
    }
}