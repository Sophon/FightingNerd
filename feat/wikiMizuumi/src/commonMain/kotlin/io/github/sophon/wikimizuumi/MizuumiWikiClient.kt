package io.github.sophon.wikimizuumi

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
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
import io.github.sophon.core.wiki.usecase.DownloadOrFetchUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveListUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveUseCase
import io.github.sophon.core.wiki.usecase.GetLastCacheInsertInstantUseCase
import io.github.sophon.wikimizuumi.data.MizuumiTables
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class MizuumiWikiClient(
    private val gameId: String,

    private val downloadOrFetchUseCase: DownloadOrFetchUseCase,

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
    private val gameTables: QueryTable = MizuumiTables.getTable(gameId)
        ?: error("$gameId not supported. Supported: ${MizuumiFeatureInfo.featureInfo.supportedGameSet}")

    override fun getFeatureInfo(): FeatureInfo = MizuumiFeatureInfo.featureInfo

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        val game = Game.fromId(gameId)

        return when (game) {
            Game.MBTL -> {
                downloadOrFetchUseCase.invoke(gameTables)
                    .map { it.keys.toList() }
                    .onSuccess { characterList ->
                        Napier.i(tag = TAG) { "$gameId - ${characterList.size} characters downloaded" }
                    }
                    .onError { Napier.e(tag = TAG) { "downloadCharacterList: $it" } }
            }
            else -> {
                downloadCharacterListUseCase.invoke(gameTables)
                    .onSuccess { characterList ->
                        Napier.i(tag = TAG) { "$gameId - ${characterList.size} characters downloaded" }
                    }
                    .onError { Napier.e(tag = TAG) { "downloadCharacterList: $it" } }
            }
        }
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
            .onError { Napier.w(tag = TAG) { "fetchCharacter: $it" } }
    }

    override suspend fun downloadMoveList(
        characterData: DownloadMoveListUseCase.CharacterData,
    ): Result<List<Move>, WikiError> {
        val game = Game.fromId(gameId)

        return when (game) {
            Game.MBTL -> {
                downloadOrFetchUseCase.invoke(gameTables)
                    .map { map ->
                        map
                            .filterKeys { it.queryName.equals(characterData.name, ignoreCase = true) }
                            .values
                            .flatten()
                    }
                    .onSuccess { moveList ->
                        Napier.d(tag = TAG) { "${characterData.name}: ${moveList.size} moves downloaded" }
                    }
                    .onError { Napier.e(tag = TAG) { "downloadMoveList: $it" } }
            }
            else -> {
                downloadMoveListUseCase.invoke(gameTables, characterData)
                    .onSuccess { moveList ->
                        Napier.d(tag = TAG) { "${characterData.name}: ${moveList.size} moves downloaded" }
                    }
                    .onError { Napier.e(tag = TAG) { "downloadMoveList: $it" } }
            }
        }
    }

    override suspend fun cacheMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        return cacheMoveListUseCase.invoke(character, moveList)
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
            .onError { Napier.w(tag = TAG) { "fetchMoveList: $it" } }
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
        const val TAG = "MizuumiWikiClient"
    }
}