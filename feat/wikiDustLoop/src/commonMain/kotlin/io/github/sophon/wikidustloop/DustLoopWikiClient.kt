package io.github.sophon.wikidustloop

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.CacheCharacterListUseCase
import io.github.sophon.core.wiki.usecase.DownloadCharacterListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterUseCase
import io.github.sophon.wikidustloop.data.DustLoopTables
import io.github.sophon.wikidustloop.domain.DustLoopFeatureInfo
import kotlinx.datetime.Instant

class DustLoopWikiClient(
    gameId: String,

    private val dustLoopFeatureInfo: DustLoopFeatureInfo,

    private val downloadCharacterListUseCase: DownloadCharacterListUseCase,
    private val cacheCharacterListUseCase: CacheCharacterListUseCase,
    private val fetchCharacterUseCase: FetchCharacterUseCase,
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
): WikiClient {
    private val gameTables: QueryTable = DustLoopTables.getTable(gameId)
        ?: error("$gameId not supported. Supported: ${DustLoopFeatureInfo.featureInfo.supportedGameSet}")

    override fun getFeatureInfo(): FeatureInfo {
        return dustLoopFeatureInfo.featureInfo
    }

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        return downloadCharacterListUseCase.invoke(gameTables)
            .onSuccess { characterList ->
                Napier.i(tag = TAG) { "${characterList.size} characters loaded" }
            }
            .onError { Napier.e(tag = TAG) { "downloadCharacterList: $it" } }
    }

    override suspend fun cacheCharacterList(
        characterList: List<Character>
    ): EmptyResult<WikiError> {
        return cacheCharacterListUseCase.invoke(characterList)
            .onError { Napier.e(tag = TAG) { "cacheCharacterList: $it" } }
    }

    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
        return fetchCharacterListUseCase.invoke()
            .onError { Napier.e(tag = TAG) { "fetchCharacterList: $it" } }
    }

    override suspend fun fetchCharacter(
        charName: String
    ): Result<Character, WikiError> {
        return fetchCharacterUseCase.invoke(charName)
            .onError { Napier.e(tag = TAG) { "fetchCharacter: $it" } }
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

    override suspend fun fetchMoveList(
        charName: String
    ): Result<List<Move>, WikiError> {
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
        const val TAG = "DustLoopWikiClient"
    }
}