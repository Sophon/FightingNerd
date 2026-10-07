package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Move
import kotlinx.coroutines.flow.Flow

internal interface MediaPort {
    fun subscribeToCharactersWithOfflineMedia(gameId: String): Flow<Set<String>>
    suspend fun save(gameId: String, characterId: String, urls: Move.Urls): EmptyResult<AppError>
    suspend fun wipe(gameId: String, characterId: String): EmptyResult<AppError>

    /**
     * Points every downloaded media of [urls] to its local file; the rest keep their remote url.
     */
    fun toOfflineUrls(gameId: String, characterId: String, urls: Move.Urls): Move.Urls
}
