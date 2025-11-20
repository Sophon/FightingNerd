package io.github.sophon.xko

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.xko.usecase.CacheCharacterListUseCase
import io.github.sophon.xko.usecase.CacheMoveListUseCase
import io.github.sophon.xko.usecase.ClearCacheUseCase
import io.github.sophon.xko.usecase.DownloadOrFetchUseCase
import io.github.sophon.xko.usecase.FetchCharacterListUseCase
import io.github.sophon.xko.usecase.FetchCharacterUseCase
import io.github.sophon.xko.usecase.FetchMoveListUseCase
import io.github.sophon.xko.usecase.FetchMoveUseCase
import io.github.sophon.xko.usecase.GetLastCacheInsertInstantUseCase
import kotlinx.datetime.Instant

internal class XkoWikiClient(
    gameId: String,

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
        TODO("Not yet implemented")
    }

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        return downloadOrFetchUseCase.invoke()
            .map { it.keys.toList() }
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

    override suspend fun fetchCharacter(charName: String): Result<Character, WikiError> {
        return fetchCharacterUseCase.invoke(charName)
            .onError { Napier.e(tag = TAG) { "fetchCharacter: $it" } }
    }

    override suspend fun downloadMoveList(charName: String): Result<List<Move>, WikiError> {
        return downloadOrFetchUseCase.invoke()
            .map { map ->
                map
                    .filterKeys { it.queryName.equals(charName, ignoreCase = true) }
                    .values
                    .flatten()
            }
            .onSuccess {
                Napier.d(tag = TAG) { "${charName}: ${it.size} moves downloaded" }
            }
            .onError { Napier.e(tag = TAG) { "downloadMoveList: $it" } }
    }

    override suspend fun cacheMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        return cacheMoveListUseCase.invoke(
            charName = character.queryName,
            moveList = moveList,
        )
            .onError { Napier.e(tag = TAG) { "cacheMoveList: $it" } }
    }

    override suspend fun fetchMoveList(charName: String): Result<List<Move>, WikiError> {
        return fetchMoveListUseCase.invoke(charName)
            .onError { Napier.e(tag = TAG) { "fetchMoveList: $it" } }
    }

    override suspend fun fetchMove(
        charName: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        return fetchMoveUseCase.invoke(charName, moveQuery)
            .onError { Napier.e(tag = TAG) { "fetchMove: $it" } }
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