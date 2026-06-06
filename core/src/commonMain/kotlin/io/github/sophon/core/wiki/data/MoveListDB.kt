package io.github.sophon.core.wiki.data

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
interface MoveListDB {
    suspend fun fetchMoveListFor(characterId: String): Result<List<Move>, WikiError>
    suspend fun fetchMoveDataFor(characterId: String, moveQuery: String): Result<Move, WikiError>
    suspend fun insertMoveList(game: Game?, character: Character, moveList: List<Move>): EmptyResult<WikiError>
    suspend fun wipe(): EmptyResult<WikiError>
    suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError>
}
