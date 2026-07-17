@file:Suppress("DEPRECATION")

package io.github.sophon.discord.feat.core.data

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import kotlinx.datetime.Instant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class InMemoryMoveListDB(private val game: Game): MoveListDB {
    private val database: MutableMap<String, Map<String, Move>> = mutableMapOf()
    private var insertTimeInstant: Instant? = null
    private val moveAliasMap: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

    override suspend fun fetchMoveListFor(
        characterId: String
    ): Result<List<Move>, WikiError> {
        val moveList = database[characterId]
            ?: return Result.Error(WikiError.UnknownCharacter(characterId)) //TODO: this should be an exception
        val result = moveList.values.toList()
        return Result.Success(result)
    }

    override suspend fun hasMovesCachedFor(characterId: String): Result<Boolean, WikiError> {
        val moveList = database[characterId]
            ?: return Result.Error(WikiError.UnknownCharacter(characterId))
        val result = moveList.isNotEmpty()
        return Result.Success(result)
    }

    override suspend fun fetchMoveDataFor(
        characterId: String,
        moveQuery: String
    ): Result<Move, WikiError> {
        val moveList = database[characterId]
            ?: return Result.Error(WikiError.UnknownCharacter(characterId))

        val moveId = moveAliasMap[characterId]?.get(moveQuery) ?: moveQuery

        val moveData = moveList[moveId]
            ?: return Result.Error(WikiError.UnknownMove(game.id, characterId, moveQuery))

        return Result.Success(moveData)
    }

    override suspend fun insertMoveList(
        game: Game?,
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        if (game == null) return Result.Error(WikiError.DatabaseError("null game"))

        val moveMap = moveList.associateBy { it.id }
        database[character.id] = moveMap

        val aliasMap = mutableMapOf<String, String>()

        moveList.forEach { move ->
            aliasMap[move.input] = move.id

            move.aliases.forEach { alias ->
                aliasMap[alias.replace(" ", "")] = move.id
            }

            move.name
                ?.lowercase()
                ?.replace(" ", "")
                ?.let { name ->
                    aliasMap[name] = move.id
                }
        }

        moveAliasMap[character.id] = aliasMap

        insertTimeInstant = Clock.System.now()

        return Result.Success(Unit)
    }

    override suspend fun wipe(): EmptyResult<WikiError> {
        database.clear()
        insertTimeInstant = null
        return Result.Success(Unit)
    }

    override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
        return Result.Success(insertTimeInstant)
    }
}