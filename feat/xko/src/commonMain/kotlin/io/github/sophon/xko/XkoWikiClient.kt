package io.github.sophon.xko

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.datetime.Instant

internal class XkoWikiClient(
    gameId: String,
): WikiClient {
    override fun getFeatureInfo(): FeatureInfo {
        TODO("Not yet implemented")
    }

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        TODO("Not yet implemented")
    }

    override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCharacter(charName: String): Result<Character, WikiError> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadMoveList(charName: String): Result<List<Move>, WikiError> {
        TODO("Not yet implemented")
    }

    override suspend fun cacheMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchMoveList(charName: String): Result<List<Move>, WikiError> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchMove(
        charName: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        TODO("Not yet implemented")
    }

    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> {
        TODO("Not yet implemented")
    }

    override suspend fun clearCache(): EmptyResult<WikiError> {
        TODO("Not yet implemented")
    }


    private companion object {
        const val TAG = "XkoWikiClient"
    }
}