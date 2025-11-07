package io.github.sophon.core.wiki.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.datetime.Instant

interface MoveListDB {
    suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiDataError>
    suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WikiDataError>
    suspend fun insertMoveList(charName: String, moveList: List<Move>): EmptyResult<WikiDataError>
    suspend fun wipe(): EmptyResult<WikiDataError>
    suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiDataError>
}