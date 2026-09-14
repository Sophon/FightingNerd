package io.github.sophon.fightingnerd.core.data

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.coroutines.flow.Flow

internal interface MediaRepo {
    fun subscribeToCharsWithOfflineMedia(gameId: String): Flow<Set<CharacterId>>
    suspend fun save(gameId: String, characterId: CharacterId, media: Move.Urls): EmptyResult<AppError>
    suspend fun wipe(gameId: String)
    suspend fun wipe(gameId: String, characterId: CharacterId)
    suspend fun createUpdatedUrls(
        gameId: String,
        characterId: CharacterId,
        media: Move.Urls,
    ): Move.Urls
}
