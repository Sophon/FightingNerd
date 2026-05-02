package io.github.sophon.fightingnerd.feat.moduleList.model

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.WikiClient
import kotlinx.coroutines.flow.Flow

internal interface WikiModule {
    val featureInfo: FeatureInfo

    fun registerGames(enabledGameList: List<Game>)

    fun getWikiClient(gameId: String): WikiClient?

    @Composable
    fun HomeScreenContent(navHostController: NavHostController)
    suspend fun onInit()

    suspend fun search(query: String)
    fun subscribeToSearchResults(): Flow<String>
}
