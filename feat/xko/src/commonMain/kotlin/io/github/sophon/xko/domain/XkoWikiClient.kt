package io.github.sophon.xko.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.usecase.CacheCharacterListUseCase
import io.github.sophon.core.wiki.usecase.CacheMoveListUseCase
import io.github.sophon.core.wiki.usecase.ClearCacheUseCase
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.core.wiki.usecase.DownloadOrFetchUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveListUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveUseCase
import io.github.sophon.core.wiki.usecase.GetLastCacheInsertInstantUseCase
import io.github.sophon.xko.integration.XkoFeatureInfo
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class XkoWikiClient(
    private val gameId: String,

    private val downloadOrFetchUseCase: DownloadOrFetchUseCase,

    private val cacheCharacterListUseCase: CacheCharacterListUseCase,
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
    private val fetchCharacterUseCase: FetchCharacterUseCase,

    private val cacheMoveListUseCase: CacheMoveListUseCase,
    private val fetchMoveListUseCase: FetchMoveListUseCase,
    private val fetchMoveUseCase: FetchMoveUseCase,

    private val getLastCacheInsertInstantUseCase: GetLastCacheInsertInstantUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
): WikiClient {
    override fun getFeatureInfo(): FeatureInfo {
        return XkoFeatureInfo.featureInfo
    }

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        return downloadOrFetchUseCase.invoke()
            .map { it.keys.toList() }
            .onSuccess { characterList ->
                Napier.i(tag = TAG) { "$gameId - ${characterList.size} characters downloaded" }
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

    override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> {
        return fetchCharacterUseCase.invoke(characterQuery)
            .onError { Napier.w(tag = TAG) { "fetchCharacter: $it" } }
    }

    override suspend fun downloadMoveList(
        characterData: DownloadMoveListUseCase.CharacterData,
    ): Result<List<Move>, WikiError> {
        return downloadOrFetchUseCase.invoke()
            .map { map ->
                map
                    .filterKeys { it.remoteQueryId.equals(characterData.name, ignoreCase = true) }
                    .values
                    .flatten()
            }
            .onSuccess {
                Napier.d(tag = TAG) { "${characterData.name}: ${it.size} moves downloaded" }
            }
            .onError { Napier.e(tag = TAG) { "downloadMoveList: $it" } }
    }

    override suspend fun cacheMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        return cacheMoveListUseCase.invoke(
            character = character,
            moveList = moveList,
        )
            .onError { Napier.e(tag = TAG) { "cacheMoveList: $it" } }
    }

    override suspend fun fetchMoveList(
        characterQuery: String,
        filter: Filter,
    ): Result<List<Move>, WikiError> {
        return fetchMoveListUseCase.invoke(characterQuery, filter)
            .onError { Napier.e(tag = TAG) { "fetchMoveList: $it" } }
    }

    override suspend fun fetchMove(
        characterQuery: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        return fetchMoveUseCase.invoke(characterQuery, moveQuery)
            .onError { Napier.w(tag = TAG) { "fetchMove: $it" } }
    }

    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> {
        return getLastCacheInsertInstantUseCase.invoke()
            .onError { Napier.e(tag = TAG) { "getLastUpdateTimeStamp: $it" } }
    }

    override suspend fun clearCache(): EmptyResult<WikiError> {
        downloadOrFetchUseCase.clearCache()
        return clearCacheUseCase.invoke()
            .onError { Napier.e(tag = TAG) { "clearCache: $it" } }
    }


    private companion object {
        const val TAG = "XkoWikiClient"
    }
}