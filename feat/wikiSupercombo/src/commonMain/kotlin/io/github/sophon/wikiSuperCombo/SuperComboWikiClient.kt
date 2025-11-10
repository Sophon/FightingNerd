package io.github.sophon.wikiSuperCombo

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikiSuperCombo.usecase.CacheCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.CacheMoveListUseCase
import io.github.sophon.wikiSuperCombo.usecase.ClearCacheUseCase
import io.github.sophon.wikiSuperCombo.usecase.DownloadCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchCharacterUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchMoveListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchMoveUseCase
import io.github.sophon.wikiSuperCombo.usecase.GetLastCacheInsertInstantUseCase
import kotlinx.datetime.Instant

interface SuperComboWikiClient {
    suspend fun downloadCharacterList(): Result<List<Character>, WikiError>
    suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError>
    suspend fun getCharacter(charName: String): Result<Character, WikiError>
    suspend fun getCharacterList(): Result<List<Character>, WikiError>

    suspend fun downloadMoveListFor(charName: String): Result<List<Move>, WikiError>
    suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError>
    suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError>
    suspend fun fetchMove(charName: String, moveQuery: String): Result<Move, WikiError>
    suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiError>

    suspend fun clearCache(): EmptyResult<WikiError>
}

internal class SuperComboWikiClientImpl(
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
): SuperComboWikiClient {
    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        return downloadCharacterListUseCase.invoke()
            .onSuccess { characterList ->
                Napier.d(tag = TAG) { "${characterList.size} characters loaded" }
            }
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
        return cacheCharacterListUseCase.invoke(characterList)
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun getCharacter(
        charName: String
    ): Result<Character, WikiError> {
        return fetchCharacterUseCase.invoke(charName)
            .onSuccess { character ->
                Napier.d(tag = TAG) { "$charName -> ${character.id}" }
            }
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun getCharacterList(): Result<List<Character>, WikiError> {
        return fetchCharacterListUseCase.invoke()
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun downloadMoveListFor(
        charName: String
    ): Result<List<Move>, WikiError> {
        return downloadMoveListUseCase.invoke(charName)
            .onSuccess { moveList ->
                Napier.d(tag = TAG) {
                    "${charName}: ${moveList.size} moves downloaded"
                }
            }
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun cacheMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        return cacheMoveListUseCase.invoke(character, moveList)
            .onError { Napier.e(tag = TAG) { it.toString() } }

    }

    override suspend fun fetchMove(
        charName: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        return fetchMoveUseCase.invoke(charName, moveQuery)
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiError> {
        return fetchMoveListUseCase.invoke(charName)
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> {
        return getLastCacheInsertInstantUseCase.invoke()
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun clearCache(): EmptyResult<WikiError> {
        return clearCacheUseCase.invoke()
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    private  companion object {
        const val TAG = "SuperComboWikiClient"
    }
}