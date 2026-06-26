package io.github.sophon.fightingnerd.core.model

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.WikiClient
import kotlinx.coroutines.flow.Flow

internal interface Module {
    val featureInfo: FeatureInfo

    suspend fun onInit()
    fun registerGames(enabledGameList: List<Game>)
    fun getWikiClient(gameId: String): WikiClient?
    suspend fun search(query: String)
    fun subscribeToSearchResults(): Flow<String>
}