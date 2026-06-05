package io.github.sophon.wikidustloop.domain

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
import io.github.sophon.wikidustloop.data.DustLoopTables
import io.github.sophon.wikidustloop.integration.DustLoopFeatureInfo
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class DustLoopWikiClient(
    private val gameId: String,

    private val dustLoopFeatureInfo: DustLoopFeatureInfo,

    private val downloadCharacterListUseCase: DownloadCharacterListUseCase,
    private val cacheCharacterListUseCase: CacheCharacterListUseCase,
    private val fetchCharacterUseCase: FetchCharacterUseCase,
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,

    private val downloadMoveListUseCase: DownloadMoveListUseCase,
    private val cacheMoveListUseCase: CacheMoveListUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val fetchMoveListUseCase: FetchMoveListUseCase,

    private val getLastCacheInsertInstantUseCase: GetLastCacheInsertInstantUseCase,
    private val fetchMoveUseCase: FetchMoveUseCase,
): WikiClient {
    private val gameTables: QueryTable = DustLoopTables.getTable(gameId)
        ?: error("$gameId not supported. Supported: ${DustLoopFeatureInfo.featureInfo.supportedGameSet}")

    override fun getFeatureInfo(): FeatureInfo {
        return dustLoopFeatureInfo.featureInfo
    }

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        return downloadCharacterListUseCase.invoke(gameTables)
            .onSuccess { characterList ->
                Napier.i(tag = TAG) { "$gameId - ${characterList.size} characters loaded" }
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

    override suspend fun downloadMoveList(
        characterData: DownloadMoveListUseCase.CharacterData,
    ): Result<List<Move>, WikiError> {
        return downloadMoveListUseCase.invoke(gameTables, characterData)
            .onSuccess { moveList ->
                Napier.d(tag = TAG) {
                    "${characterData.name}: ${moveList.size} moves downloaded"
                }
            }
            .onError {
                Napier.e(tag = TAG) { "downloadMoveList(${characterData.name}): $it" }
            }
    }

    override suspend fun cacheMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        return cacheMoveListUseCase.invoke(character, moveList)
            .onError {
                Napier.e(tag = TAG) { "cacheMoveList(${character.id}, ${moveList.size}): $it" }
            }

    }

    override suspend fun fetchMoveList(
        charName: String,
        filter: Filter,
    ): Result<List<Move>, WikiError> {
        return fetchMoveListUseCase.invoke(charName, filter)
            .onError {
                Napier.e(tag = TAG) { "fetchMoveList($charName): $it" }
            }
    }

    override suspend fun fetchMove(
        charName: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        return fetchMoveUseCase.invoke(charName, moveQuery)
            .onError {
                Napier.w(tag = TAG) { "fetchMove($charName, $moveQuery): $it" }
            }
    }

    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> {
        return getLastCacheInsertInstantUseCase.invoke()
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun clearCache(): EmptyResult<WikiError> {
        return clearCacheUseCase.invoke()
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }


    private companion object {
        const val TAG = "DustLoopWikiClient"
    }
}