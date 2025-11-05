package io.github.sophon.wikiSuperCombo

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikiSuperCombo.domain.SuperComboError
import kotlinx.datetime.Instant

interface SuperComboWikiClient {
    suspend fun downloadCharacterList(): Result<List<Character>, SuperComboError>
    suspend fun downloadMoveListFor(charName: String): Result<List<Move>, SuperComboError>
    suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<SuperComboError>
    suspend fun getLastUpdateTimeStamp(): Result<Instant?, SuperComboError>
    suspend fun clearCache(): EmptyResult<SuperComboError>

    suspend fun frameDataFor(charName: String, moveQuery: String): Result<Move, SuperComboError>
    suspend fun getCharacterList(): Result<List<Character>, SuperComboError>
}

internal class SuperComboWikiClientImpl(
    //
): SuperComboWikiClient {
    override suspend fun downloadCharacterList(): Result<List<Character>, SuperComboError> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadMoveListFor(
        charName: String
    ): Result<List<Move>, SuperComboError> {
        TODO("Not yet implemented")
    }

    override suspend fun cacheMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<SuperComboError> {
        TODO("Not yet implemented")
    }

    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, SuperComboError> {
        TODO("Not yet implemented")
    }

    override suspend fun clearCache(): EmptyResult<SuperComboError> {
        TODO("Not yet implemented")
    }

    override suspend fun frameDataFor(
        charName: String,
        moveQuery: String,
    ): Result<Move, SuperComboError> {
        TODO("Not yet implemented")
    }

    override suspend fun getCharacterList(): Result<List<Character>, SuperComboError> {
        TODO("Not yet implemented")
    }
}