package io.github.sophon.wikimizuumi

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
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
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class MizuumiWikiClient(
    private val gameId: String,

    //feature info

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

    override suspend fun downloadMoveList(characterData: DownloadMoveListUseCase.CharacterData): Result<List<Move>, WikiError> {
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
}