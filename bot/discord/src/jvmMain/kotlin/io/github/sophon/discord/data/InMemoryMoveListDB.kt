@file:Suppress("DEPRECATION")

package io.github.sophon.discord.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.datetime.Instant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class InMemoryMoveListDB: MoveListDB {
    private lateinit var game: Game
    private val database: MutableMap<String, Map<String, Move>> = mutableMapOf()
    private var insertTimeInstant: Instant? = null
    private val charNameAliasMap: MutableMap<String, String> = mutableMapOf()
    private val moveAliasMap: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

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
        val characterId = charNameAliasMap[charName] ?: charName

        val moveList = database[characterId]
            ?: return Result.Error(WikiError.UnknownCharacter(charName))

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

        this.game = game
        val moveMap = moveList.associateBy { it.input }
        database[character.id] = moveMap

        val aliasMap = mutableMapOf<String, String>()

        moveList.forEach { move ->
            val moveId = move.input
            aliasMap[moveId] = moveId

            move.aliases.forEach { alias ->
                aliasMap[alias] = moveId
            }

            move.name?.lowercase()?.let { name ->
                aliasMap[name] = moveId
            }
        }

        moveAliasMap[character.id] = aliasMap

        insertTimeInstant = Clock.System.now()

        charNameAliasMap[character.id] = character.id
        character.aliasList.forEach { alias ->
            charNameAliasMap.putIfAbsent(alias, character.id)
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