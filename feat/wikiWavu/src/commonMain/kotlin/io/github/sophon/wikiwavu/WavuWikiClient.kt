package io.github.sophon.wikiwavu

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikiwavu.usecase.CacheCharacterListUseCase
import io.github.sophon.wikiwavu.usecase.CacheMoveListUseCase
import io.github.sophon.wikiwavu.usecase.ClearCacheUseCase
import io.github.sophon.wikiwavu.usecase.DownloadCharacterListUseCase
import io.github.sophon.wikiwavu.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiwavu.usecase.FetchCharacterListUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveDataUseCase
import io.github.sophon.wikiwavu.usecase.FetchMoveListUseCase
import io.github.sophon.wikiwavu.usecase.GetFeatureInfoUseCase
import io.github.sophon.wikiwavu.usecase.GetLastCacheInsertInstantUseCase
import kotlinx.datetime.Instant

internal class WavuWikiClient(
    private val getFeatureInfoUseCase: GetFeatureInfoUseCase,

    private val downloadCharacterListUseCase: DownloadCharacterListUseCase,
    private val cacheCharacterListUseCase: CacheCharacterListUseCase,
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,

    private val downloadMoveListUseCase: DownloadMoveListUseCase,
    private val cacheMoveListUseCase: CacheMoveListUseCase,
    private val getLastCacheInsertInstantUseCase: GetLastCacheInsertInstantUseCase,
    private val fetchMoveDataUseCase: FetchMoveDataUseCase,
    private val fetchMoveListUseCase: FetchMoveListUseCase,

    private val clearCacheUseCase: ClearCacheUseCase,
): WikiClient {
    override fun getFeatureInfo(): FeatureInfo {
        return getFeatureInfoUseCase.invoke()
    }

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        return downloadCharacterListUseCase.invoke()
            .onSuccess { Napier.d(tag = TAG) { "${it.size} characters loaded" } }
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

    override suspend fun fetchCharacter(charName: String): Result<Character, WikiError> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadMoveList(
        charName: String
    ): Result<List<Move>, WikiError> {
        return downloadMoveListUseCase.invoke(charName)
            .onSuccess {
                Napier.d(tag = TAG) { "${charName}: ${it.size} moves downloaded" }
            }
            .onError { Napier.e(tag = TAG) { "downloadMoveList(${charName}): $it" } }
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
        charName: String,
    ): Result<List<Move>, WikiError> {
        return fetchMoveListUseCase.invoke(charName)
            .onError { Napier.e(tag = TAG) { "fetchMoveList($charName): $it" } }
    }

    override suspend fun fetchMove(
        charName: String,
        moveQuery: String
    ): Result<Move, WikiError> {
        return fetchMoveDataUseCase.invoke(charName, moveQuery)
            .onError {
                Napier.e(tag = TAG) { "fetchMove($charName, $moveQuery): $it" }
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