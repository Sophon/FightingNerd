package io.github.sophon.core.wiki.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.datetime.Instant

interface MoveListDB {
    suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiError>
    suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WikiError>
    suspend fun insertMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError>
    suspend fun wipe(): EmptyResult<WikiError>
    suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError>
}