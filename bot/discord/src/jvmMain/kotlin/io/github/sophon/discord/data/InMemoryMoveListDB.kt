@file:Suppress("DEPRECATION")

package io.github.sophon.discord.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.datetime.Instant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class InMemoryMoveListDB: MoveListDB {
    private val database: MutableMap<String, Map<String, Move>> = mutableMapOf()
    private var insertTimeInstant: Instant? = null
    private val charNameAliasMap: MutableMap<String, String> = mutableMapOf()
    private val moveAliasMap: MutableMap<String, String> = mutableMapOf()

    override suspend fun fetchMoveListFor(
        charName: String
    ): Result<List<Move>, WikiError> {
        val characterId = if (database.containsKey(charName)) {
            charName
        } else {
            charNameAliasMap[charName]
        }
        if (characterId == null)
            return Result.Error(WikiError.UnknownCharacter(charName))

        val moveList = database[characterId]
            ?: return Result.Error(WikiError.UnknownCharacter(charName))

        return Result.Success(moveList.values.toList())
    }

    override suspend fun fetchMoveDataFor(
        charName: String,
        moveQuery: String
    ): Result<Move, WikiError> {
        val characterId = if (database.containsKey(charName)) {
            charName
        } else {
            charNameAliasMap[charName]
        }
        if (characterId == null) return Result.Error(WikiError.UnknownCharacter(charName))

        val moveList = database[characterId]
            ?: return Result.Error(WikiError.UnknownCharacter(charName))

        val moveId = moveAliasMap[moveQuery]

        val moveData = moveList[moveId]
            ?: return Result.Error(WikiError.UnknownMove(charName, moveQuery))

        return Result.Success(moveData)
    }

    //TODO: pass an alias list instead of handling the formatting here
    override suspend fun insertMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        val moveMap = buildMap {
            moveList.forEach { move ->
                put(move.input, move)
                moveAliasMap[move.input] = move.input
                move.aliases.forEach { alias ->
                    moveAliasMap[alias] = move.input
                }
                move.name
                    ?.lowercase()
                    ?.let { moveAliasMap[it] = move.input }
            }
        }
        database[character.id] = moveMap
        insertTimeInstant = Clock.System.now()

        charNameAliasMap[character.id] = character.id
        character.aliasList.forEach { alias ->
            if (charNameAliasMap.containsKey(alias).not()) {
                charNameAliasMap[alias] = character.id
            }
        }

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