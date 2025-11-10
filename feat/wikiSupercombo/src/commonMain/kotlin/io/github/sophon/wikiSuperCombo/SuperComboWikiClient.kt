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
import io.github.sophon.wikiSuperCombo.usecase.DownloadCharacterListUseCase
import io.github.sophon.wikiSuperCombo.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiSuperCombo.usecase.FetchCharacterUseCase

interface SuperComboWikiClient {
    suspend fun downloadCharacterList(): Result<List<Character>, WikiError>
    suspend fun cacheCharaterList(characterList: List<Character>): EmptyResult<WikiError>
    suspend fun getCharacter(charName: String): Result<Character, WikiError>

    suspend fun downloadMoveListFor(charName: String): Result<List<Move>, WikiError>
//    suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<SuperComboError>
//    suspend fun getLastUpdateTimeStamp(): Result<Instant?, SuperComboError>
//    suspend fun clearCache(): EmptyResult<SuperComboError>
//
//    suspend fun frameDataFor(charName: String, moveQuery: String): Result<Move, SuperComboError>
}

internal class SuperComboWikiClientImpl(
    private val downloadCharacterListUseCase: DownloadCharacterListUseCase,
    private val cacheCharacterListUseCase: CacheCharacterListUseCase,
    private val fetchCharacterUseCase: FetchCharacterUseCase,
    private val downloadMoveListUseCase: DownloadMoveListUseCase,
): SuperComboWikiClient {
    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        return downloadCharacterListUseCase.invoke()
            .onSuccess { characterList ->
                Napier.d(tag = TAG) { "${characterList.size} characters loaded" }
            }
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun cacheCharaterList(characterList: List<Character>): EmptyResult<WikiError> {
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

//    override suspend fun cacheMoveList(
//        character: Character,
//        moveList: List<Move>,
//    ): EmptyResult<SuperComboError> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, SuperComboError> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun clearCache(): EmptyResult<SuperComboError> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun frameDataFor(
//        charName: String,
//        moveQuery: String,
//    ): Result<Move, SuperComboError> {
//        TODO("Not yet implemented")
//    }


    private  companion object {
        const val TAG = "SuperComboWikiClient"
    }
}