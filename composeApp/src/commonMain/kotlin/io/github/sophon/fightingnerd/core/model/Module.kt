package io.github.sophon.fightingnerd.core.model

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.WikiClient
import kotlinx.coroutines.flow.Flow

internal interface Module {
    val featureInfo: FeatureInfo

    suspend fun onInit()
    fun registerGames(enabledGameList: List<Game>)
    fun getWikiClient(gameId: String): WikiClient?
    suspend fun search(query: String)
    fun subscribeToSearchResults(): Flow<String>
}