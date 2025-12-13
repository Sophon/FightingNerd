package io.github.sophon.wikiwavu

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
import io.github.sophon.wikiwavu.domain.WavuFeatureInfo
import io.github.sophon.wikiwavu.util.cleanMoveInput
import kotlinx.datetime.Instant

internal class WavuWikiClient(
    gameId: String,

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
            .onSuccess { Napier.i(tag = TAG) { "${it.size} characters loaded" } }
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
        charName: String
    ): Result<Character, WikiError> {
        return fetchCharacterUseCase.invoke(charName)
            .onError { Napier.w(tag = TAG) { "fetchCharacter(${charName}): $it" } }
    }

    override suspend fun downloadMoveList(
        characterData: DownloadMoveListUseCase.CharacterData,
    ): Result<List<Move>, WikiError> {
        return downloadMoveListUseCase.invoke(queryTable, characterData)
            .onSuccess {
                Napier.i(tag = TAG) { "${characterData.name}: ${it.size} moves downloaded" }
            }
            .onError { Napier.e(tag = TAG) { "downloadMoveList(${characterData.name}): $it" } }
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
        return fetchMoveUseCase.invoke(charName, moveQuery.cleanMoveInput(keepSpaces = true))
            .onError {
                Napier.w(tag = TAG) { "fetchMove($charName, $moveQuery): $it" }
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