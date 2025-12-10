package io.github.sophon.core.wiki.domain

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import kotlinx.datetime.Instant

interface WikiClient {
    fun getFeatureInfo(): FeatureInfo

    suspend fun downloadCharacterList(): Result<List<Character>, WikiError>
    suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError>
    suspend fun fetchCharacterList(): Result<List<Character>, WikiError>
    suspend fun fetchCharacter(charName: String): Result<Character, WikiError>

    suspend fun downloadMoveList(characterData: DownloadMoveListUseCase.CharacterData): Result<List<Move>, WikiError>
    suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError>
    suspend fun fetchMoveList(charName: String): Result<List<Move>, WikiError>
    suspend fun fetchMove(charName: String, moveQuery: String): Result<Move, WikiError>

    suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError>
    suspend fun clearCache(): EmptyResult<WikiError>
}