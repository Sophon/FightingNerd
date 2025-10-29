package com.example.cornerman.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val vm = koinViewModel<HomeVM>()
    val state by vm.state.collectAsStateWithLifeCycle()
}