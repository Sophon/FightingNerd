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
    private val aliasMap: MutableMap<String, String> = mutableMapOf()

    override suspend fun fetchMoveListFor(
        charName: String
    ): Result<List<Move>, WikiError> {
        val characterId = if (database.containsKey(charName)) {
            charName
        } else {
            aliasMap[charName]
        }
        if (characterId == null)
            return Result.Error(WikiError.UnknownCharacter(charName))

        return database[characterId]
            ?.let { Result.Success(it.values.toList()) }
            ?: Result.Error(WikiError.UnknownCharacter(charName))
    }

    override suspend fun fetchMoveDataFor(
        charName: String,
        moveQuery: String
    ): Result<Move, WikiError> {
        val characterId = if (database.containsKey(charName)) {
            charName
        } else {
            aliasMap[charName]
        }
        if (characterId == null)
            return Result.Error(WikiError.UnknownCharacter(charName))

        val moveList = database[characterId]
            ?: return Result.Error(WikiError.UnknownCharacter(charName))
        val moveData = moveList[moveQuery]
            ?: return Result.Error(WikiError.UnknownMove(charName, moveQuery))

        return Result.Success(moveData)
    }

    override suspend fun insertMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        val moveMap = buildMap {
            moveList.forEach { move ->
                put(move.input, move)
            }
        }
        database[character.id] = moveMap
        insertTimeInstant = Clock.System.now()

        aliasMap[character.id] = character.id
        character.aliasList.forEach { alias ->
            if (database.containsKey(alias).not()) {
                aliasMap[alias] = character.id
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