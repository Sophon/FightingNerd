package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.GetAvailableGamesUseCase
import io.github.sophon.wiki.app.outPort.LoadWikiConfigPort
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal class GetAvailableGamesService(
    private val loadWikiConfigPort: LoadWikiConfigPort,
) : GetAvailableGamesUseCase {
    override fun invoke(): Flow<Set<Game>> {
        val flow = loadWikiConfigPort
            .subscribe()
            .filterNotNull()
            .map { wikiConfig -> wikiConfig.availableGameSet }
            .distinctUntilChanged()
        return flow
    }
}
