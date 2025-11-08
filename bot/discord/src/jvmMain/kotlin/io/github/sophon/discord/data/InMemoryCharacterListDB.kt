@file:Suppress("DEPRECATION")

package io.github.sophon.discord.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import kotlinx.datetime.Instant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class InMemoryCharacterListDB: CharacterListDB {
    private var database: MutableMap<String, Character> = mutableMapOf()
    private var insertTimeInstant: Instant? = null

    override suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
        characterList.forEach { character ->
            database.put(key = character.id, value = character)
            character.aliasList.forEach { alias ->
                database.put(key = alias, value = character)
            }
        }
        insertTimeInstant = Clock.System.now()
        return Result.Success(Unit)
    }

    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
        return Result.Success(database.values.toList())
    }

    override suspend fun wipe(): EmptyResult<WikiError> {
        database.clear()
        insertTimeInstant = null
        return Result.Success(Unit)
    }

    override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
        return Result.Success(insertTimeInstant)
    }

    override suspend fun fetchCharacterDataFor(charName: String): Result<Character, WikiError> {
        val character: Character = database[charName]
            ?: return Result.Error(WikiError.UNKNOWN_CHARACTER)
        return Result.Success(character)
    }
}