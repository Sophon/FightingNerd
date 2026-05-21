package io.github.sophon.fightingnerd.feat.home.model

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface HomeScreenModule {
    @Composable
    fun HomeScreenContent(navHostController: NavHostController)
}