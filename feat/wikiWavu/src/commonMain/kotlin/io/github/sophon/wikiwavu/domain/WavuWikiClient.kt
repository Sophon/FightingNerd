package io.github.sophon.wikiwavu.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.usecase.CacheCharacterListUseCase
import io.github.sophon.core.wiki.usecase.CacheMoveListUseCase
import io.github.sophon.core.wiki.usecase.ClearCacheUseCase
import io.github.sophon.core.wiki.usecase.DownloadCharacterListUseCase
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveListUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveUseCase
import io.github.sophon.core.wiki.usecase.GetLastCacheInsertInstantUseCase
import io.github.sophon.wikiwavu.data.WavuTables
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class WavuWikiClient(
    private val gameId: String,

    private val wavuFeatureInfo: WavuFeatureInfo,

    private val downloadCharacterListUseCase: DownloadCharacterListUseCase,
    private val cacheCharacterListUseCase: CacheCharacterListUseCase,
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
    private val fetchCharacterUseCase: FetchCharacterUseCase,

    private val downloadMoveListUseCase: DownloadMoveListUseCase,
    private val cacheMoveListUseCase: CacheMoveListUseCase,
    private val fetchMoveUseCase: FetchMoveUseCase,
    private val fetchMoveListUseCase: FetchMoveListUseCase,

    private val getLastCacheInsertInstantUseCase: GetLastCacheInsertInstantUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
): WikiClient {
    private val queryTable: QueryTable = WavuTables.getTable(gameId)
        ?: error("$gameId not supported. Supported: ${WavuFeatureInfo.featureInfo.supportedGameSet}")

    override fun getFeatureInfo(): FeatureInfo {
        return wavuFeatureInfo.featureInfo
    }

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        return downloadCharacterListUseCase.invoke(queryTable)
            .onSuccess { Napier.i(tag = TAG) { "$gameId - ${it.size} characters loaded" } }
            .onError { Napier.e(tag = TAG) { "downloadCharacterList: $it" } }
    }

    override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
        return cacheCharacterListUseCase.invoke(characterList)
            .onError { Napier.e(tag = TAG) { "cacheCharacterList: $it" } }
    }

    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
        return fetchCharacterListUseCase.invoke()
            .onError { Napier.e(tag = TAG) { "fetchCharacterList: $it" } }
    }

    override suspend fun fetchCharacter(
        characterQuery: String
    ): Result<Character, WikiError> {
        return fetchCharacterUseCase.invoke(characterQuery)
            .onError { Napier.w(tag = TAG) { "fetchCharacter(${characterQuery}): $it" } }
    }

    override suspend fun downloadMoveListFor(
        character: Character,
    ): Result<List<Move>, WikiError> {
        return downloadMoveListUseCase.invoke(queryTable, character)
            .onSuccess {
                Napier.d(tag = TAG) { "${character.displayName}: ${it.size} moves downloaded" }
            }
            .onError {
                Napier.e(tag = TAG) { "downloadMoveList(${character.remoteQueryId}): $it" }
            }
    }

    override suspend fun cacheMoveList(
        character: Character,
        moveList: List<Move>
    ): EmptyResult<WikiError> {
        return cacheMoveListUseCase.invoke(character, moveList)
            .onError {
                Napier.e(tag = TAG) { "cacheMoveList(${character.id}, ${moveList.size}): $it" }
            }
    }

    override suspend fun fetchMoveList(
        characterQuery: String,
        filter: Filter,
    ): Result<List<Move>, WikiError> {
        return fetchMoveListUseCase.invoke(characterQuery, filter)
            .onError { Napier.e(tag = TAG) { "fetchMoveList($characterQuery): $it" } }
    }

    override suspend fun fetchMove(
        characterQuery: String,
        moveQuery: String
    ): Result<Move, WikiError> {
        return fetchMoveUseCase.invoke(characterQuery, moveQuery.cleanMoveInput(keepSpaces = true))
            .onError {
                Napier.w(tag = TAG) { "fetchMove($characterQuery, $moveQuery): $it" }
            }
    }

    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> {
        return getLastCacheInsertInstantUseCase.invoke()
            .onError { Napier.e(tag = TAG) { "getLastUpdateTimeStamp: $it" } }
    }

    override suspend fun clearCache(): EmptyResult<WikiError> {
        return clearCacheUseCase.invoke()
            .onError { Napier.e(tag = TAG) { "clearCache: $it" } }
    }


    private companion object {
        const val TAG = "WavuWikiClient"
    }
}